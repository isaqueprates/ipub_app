package com.example.ipub_app.data

import android.content.Context
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth

class FirebaseRepository(val context: Context? = null) {

    private val db = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = Firebase.auth
    private var memberListener: ListenerRegistration? = null

    // 🔹 Adiciona um membro ao Firestore
    suspend fun addMember(member: Member): Boolean { // ⬅️ Agora retorna Boolean
        return try {
            db.collection("members")
                .document(member.id)
                .set(member)
                .await()
            true // ⬅️ Sucesso
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("FirebaseRepository", "Falha ao adicionar membro: ${e.message}")
            false // ⬅️ Falha
        }
    }

//     🔹 Cria usuário e salva no Firestore
    suspend fun createUser(email: String, password: String, name: String, role: String = "member"): Boolean {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: return false

            val userData = hashMapOf(
                "uid" to user.uid,
                "email" to email,
                "name" to name,
                "role" to role
            )

            db.collection("users").document(user.uid).set(userData).await()
            Log.d("FirebaseRepository", "✅ Usuário salvo no Firestore: $email")
            true
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "❌ Erro ao criar usuário: ${e.message}", e)
            false
        }
    }

    // 🔹 Escuta todos os usuários em tempo real (usado no AdminActivity)
    fun listenToUsers(onUpdate: (List<Map<String, Any>>) -> Unit) {
        db.collection("users")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("FirebaseRepository", "❌ Erro ao escutar usuários: ${e.message}", e)
                    return@addSnapshotListener
                }

                val users = snapshot?.documents?.mapNotNull { it.data } ?: emptyList()
                Log.d("FirebaseRepository", "📡 Atualização recebida: ${users.size} usuários")
                onUpdate(users)
            }
    }

    // 🔹 Ouve mudanças em tempo real no Firestore e envia para o ViewModel
    fun listenToChanges(onUpdate: (List<Member>) -> Unit) {
        memberListener = db.collection("members")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    error.printStackTrace()
                    return@addSnapshotListener
                }

                val members = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Member::class.java)
                } ?: emptyList()
                onUpdate(members)
            }
    }

    // 🔹 Atualiza a função de um usuário
    suspend fun updateUserRole(uid: String, newRole: String) {
        try {
            db.collection("users").document(uid).update("role", newRole).await()
            Log.d("FirebaseRepository", "🔄 Função atualizada para $newRole (uid=$uid)")
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "❌ Erro ao atualizar função: ${e.message}", e)
        }
    }

    // 🔹 Remove listener ao fechar o ViewModel
    fun stopListening() {
        memberListener?.remove()
        memberListener = null
    }

    // 🔹 Deleta membro pelo nome (caso não tenha o uid no Room)
    suspend fun deleteMemberByName(name: String) {
        try {
            val snapshot = db.collection("members")
                .whereEqualTo("name", name)
                .get()
                .await()

            for (doc in snapshot.documents) {
                db.collection("members").document(doc.id).delete().await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 🔹 Exclui um usuário do Firestore
    suspend fun deleteUser(uid: String) {
        try {
            db.collection("users").document(uid).delete().await()
            Log.d("FirebaseRepository", "🗑️ Usuário deletado (uid=$uid)")
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "❌ Erro ao deletar usuário: ${e.message}", e)
        }
    }
}

//package com.example.ipub_app.data
//
//import android.content.Context
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.ktx.userProfileChangeRequest
//import com.google.firebase.firestore.FirebaseFirestore
//import kotlinx.coroutines.tasks.await
//
//class FirebaseRepository(private val context: Context) {
//    private val auth = FirebaseAuth.getInstance()
//    private val db = FirebaseFirestore.getInstance()
//
//    // 🔹 Cria um novo usuário (Auth + Firestore)
//    suspend fun createUser(email: String, password: String, name: String, role: String): Boolean {
//        return try {
//            val result = auth.createUserWithEmailAndPassword(email, password).await()
//            val uid = result.user?.uid ?: return false
//
//            result.user?.updateProfile(userProfileChangeRequest { displayName = name })?.await()
//
//            val userData = mapOf(
//                "uid" to uid,
//                "name" to name,
//                "email" to email,
//                "role" to role
//            )
//            db.collection("users").document(uid).set(userData).await()
//            true
//        } catch (e: Exception) {
//            e.printStackTrace()
//            false
//        }
//    }
//
//    // 🔹 Observa a lista de usuários em tempo real
//    fun listenToUsers(onUpdate: (List<Map<String, Any>>) -> Unit) {
//        db.collection("users").addSnapshotListener { snapshot, error ->
//            if (error != null) return@addSnapshotListener
//            val users = snapshot?.documents?.mapNotNull { it.data } ?: emptyList()
//            onUpdate(users)
//        }
//    }
//
//    // 🔹 Atualiza o papel do usuário
//    suspend fun updateUserRole(uid: String, newRole: String) {
//        try {
//            db.collection("users").document(uid)
//                .update("role", newRole)
//                .await()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    // 🔹 Deleta usuário
//    suspend fun deleteUser(uid: String) {
//        try {
//            db.collection("users").document(uid).delete().await()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//}