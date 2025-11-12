////package com.example.ipub_app.viewmodel
////
////import android.app.Application
////import android.util.Log
////import androidx.lifecycle.AndroidViewModel
////import androidx.lifecycle.viewModelScope
////import com.example.ipub_app.data.AppDatabase
////import com.example.ipub_app.data.Member
////import com.example.ipub_app.data.MemberRepository
////import com.example.ipub_app.data.FirebaseRepository
////import kotlinx.coroutines.flow.Flow
////import kotlinx.coroutines.flow.SharingStarted
////import kotlinx.coroutines.flow.stateIn
////import kotlinx.coroutines.launch
////
////class MemberViewModel(application: Application) : AndroidViewModel(application) {
////
////    private val memberRepo: MemberRepository
////    private val firebaseRepo = FirebaseRepository(application)
////
////    init {
////        val db = AppDatabase.getDatabase(application)
////        memberRepo = MemberRepository(db.memberDao())
////
////        // 🔥 Sincroniza Firestore → Room (em tempo real)
////        firebaseRepo.listenToChanges { membersFromCloud ->
////            viewModelScope.launch {
////                Log.d("VM", "Iniciando inserção de ${membersFromCloud.size} membros no Room.") // VERIFIQUE este log
////                membersFromCloud.forEach { member ->
////                    memberRepo.insert(member)
////                }
////            }
////        }
////    }
////
////    // 🔹 Fluxo de todos os membros locais (Room)
////    val members = memberRepo.getAllFlow()
////        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
////
////    // 🔹 Insere membro local + Firestore
////    fun insert(name: String, role: String, department: String, birthday: String) {
////        viewModelScope.launch {
////            val uid = java.util.UUID.randomUUID().toString()
////            val member = Member(
////                uid = uid,
////                name = name,
////                role = role,
////                department = department,
////                birthday = birthday
////            )
////
////            memberRepo.insert(member)       // 💾 Local
////            firebaseRepo.addMember(member)  // ☁️ Nuvem
////        }
////    }
////
////    // 🔹 Deleta local + nuvem
////    fun delete(member: Member) {
////        viewModelScope.launch {
////            memberRepo.delete(member)
////            firebaseRepo.deleteMemberByName(member.name)
////        }
////    }
////
////    // 🔹 Filtra por departamento
////    fun getMembersByDepartment(department: String): Flow<List<Member>> {
////        return memberRepo.getByDepartmentFlow(department)
////    }
////
////    override fun onCleared() {
////        super.onCleared()
////        firebaseRepo.stopListening() // ✅ evita memory leak
////    }
////}
//
//package com.example.ipub_app.viewmodel
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.ipub_app.data.FirebaseMemberRepository
//import com.example.ipub_app.data.Member
//import kotlinx.coroutines.flow.SharingStarted
//import kotlinx.coroutines.flow.stateIn
//import kotlinx.coroutines.flow.map
//import kotlinx.coroutines.launch
//
//class MemberViewModel : ViewModel() {
//    private val repo = FirebaseMemberRepository()
//
//    // Fluxo com todos os membros
//    val members = repo.getAllFlow()
//        .map { it.sortedBy { m -> m.name } }
//        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
//
//    fun insert(name: String, role: String, department: String, birthday: String) {
//        viewModelScope.launch {
//            repo.insert(Member(name = name, role = role, department = department, birthday = birthday))
//        }
//    }
//
//    fun delete(memberId: Member) {
//        viewModelScope.launch { repo.delete(memberId) }
//    }
//
//    fun getMembersByDepartment(department: String) = repo.getByDepartmentFlow(department)
//}

package com.example.ipub_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ipub_app.data.FirebaseMemberRepository
import com.example.ipub_app.data.Member
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MemberViewModel : ViewModel() {

    private val repo = FirebaseMemberRepository()

    // 🔹 Fluxo em tempo real com todos os membros
    val members = repo.getAllFlow()
        .map { list -> list.sortedBy { it.name } } // ordena por nome
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // 🔹 Adiciona novo membro
    fun insert(name: String, role: String, department: String, birthday: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val member = Member(name = name, role = role, department = department, birthday = birthday)
                val success = repo.insert(member)
                onResult(success, if (success) "Membro criado!" else null)
            } catch (e: SecurityException) {
                onResult(false, e.message)
            }
        }
    }

    // 🔹 Remove membro
    fun delete(memberId: String) {
        viewModelScope.launch {
            repo.delete(memberId)
        }
    }

    // 🔹 Retorna fluxo filtrado por departamento
    fun getMembersByDepartment(department: String) =
        repo.getByDepartmentFlow(department)
}
