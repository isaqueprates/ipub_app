package com.ipub.ipub_app.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirebaseMemberRepository {

    private val db = FirebaseFirestore.getInstance()
    private val membersRef = db.collection("members")
    private val currentUser = FirebaseAuth.getInstance().currentUser

    suspend fun memberExists(name: String, birthday: String): Boolean {
        return try {
            val querySnapshot = membersRef
                .whereEqualTo("name", name.trim())       // 🔹 Verifica o nome
                .whereEqualTo("birthday", birthday.trim()) // 🔹 Verifica o aniversário
                .get()
                .await()

            // Retorna true se houver pelo menos um documento que satisfaça AMBAS as condições
            !querySnapshot.isEmpty
        } catch (e: Exception) {
            e.printStackTrace()
            // Em caso de erro, assume que não existe para tentar salvar
            false
        }
    }

    // 🔹 Adiciona ou atualiza um membro
    suspend fun insert(member: Member): Boolean {
        return try {
            val docRef = if (member.id.isEmpty()) membersRef.document() else membersRef.document(member.id)
            val newId = docRef.id
            val data = member.copy(id = newId, createdBy = currentUser?.uid ?: "")
            docRef.set(data).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // 🔹 Remove membro por ID
    suspend fun delete(memberId: String): Boolean {
        return try {
            membersRef.document(memberId).delete().await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // 🔹 Retorna fluxo com todos os membros em tempo real
    fun getAllFlow(): Flow<List<Member>> = callbackFlow {
        val listener = membersRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val list = snapshot?.documents?.mapNotNull {
                it.toObject(Member::class.java)
            } ?: emptyList()

            trySend(list).isSuccess
        }
        awaitClose { listener.remove() }
    }

    // 🔹 Retorna fluxo de membros filtrados por departamento
    fun getByDepartmentFlow(department: String): Flow<List<Member>> = callbackFlow {
        val listener = membersRef
            .whereEqualTo("department", department)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val list = snapshot?.documents?.mapNotNull {
                    it.toObject(Member::class.java)
                } ?: emptyList()

                trySend(list).isSuccess
            }
        awaitClose { listener.remove() }
    }

    // 🔹 BUSCA membro por ID (corrigido, usando membersRef)
    fun getMemberById(id: String): Flow<Member?> =
        membersRef
            .document(id)
            .snapshots()
            .map { it.toObject(Member::class.java) }

    // 🔹 Atualiza membro (corrigido)
    suspend fun update(member: Member) {
        membersRef
            .document(member.id)
            .set(member)
            .await()
    }
}