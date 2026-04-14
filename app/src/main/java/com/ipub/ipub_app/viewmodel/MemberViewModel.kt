package com.ipub.ipub_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipub.ipub_app.data.FirebaseMemberRepository
import com.ipub.ipub_app.data.Member
import kotlinx.coroutines.flow.Flow
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
    fun insert(
        name: String,
        role: String,
        department: String,
        birthday: String,
        dateBatism: String,
        dateEspirit: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            // 🔹 PASSO 1: Verificar a existência combinando NOME e ANIVERSÁRIO
            val exists = repo.memberExists(name, birthday) // Passa ambos os parâmetros

            if (exists) {
                onResult(false, "Erro: Este membro já está cadastrado com a mesma data de aniversário.")
                return@launch
            }

            // 🔹 PASSO 2: Se não existir, prossegue com o salvamento
            try {
                val member = Member(
                    name = name,
                    role = role,
                    department = department,
                    birthday = birthday,
                    dateBatism = dateBatism,
                    dateEspirit = dateEspirit
                )
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

    // 🔹 BUSCA membro por ID — agora dentro da classe ✔
    fun getMemberById(id: String): Flow<Member?> {
        return repo.getMemberById(id)
    }

    // 🔹 ATUALIZA membro — agora dentro da classe ✔
    fun update(member: Member) {
        viewModelScope.launch {
            repo.update(member)
        }
    }
}
