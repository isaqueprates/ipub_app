package com.ipub.ipub_app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ipub.ipub_app.data.Member
import com.ipub.ipub_app.data.MemberItem // Reutilize o seu MemberItem existente
import com.ipub.ipub_app.ui.theme.Ipub_appTheme
import com.ipub.ipub_app.viewmodel.MemberViewModel
import androidx.compose.material.icons.filled.Clear
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext

class SearchActivity : ComponentActivity() {

    private val viewModel: MemberViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Ipub_appTheme {

                val context = LocalContext.current
                // Estado local para a consulta de busca e resultados
                var searchQuery by remember { mutableStateOf("") }

                val allMembers by viewModel.members.collectAsState()

                SearchScreen(
                    searchQuery = searchQuery,
                    onQueryChange = { searchQuery = it },
                    members = allMembers.filter { // Filtro simples local
                        it.name.contains(searchQuery, ignoreCase = true) ||
                                it.department.contains(searchQuery, ignoreCase = true) ||
                                it.role.contains(searchQuery, ignoreCase = true)
                    },
                    onBack = { finish() },
                    onEdit = { member ->
                        // 🔹 AÇÃO DE EDITAR: Abre a EditMemberActivity passando o ID
                        val intent = Intent(context, EditMemberActivity::class.java)
                        intent.putExtra("memberId", member.id)
                        context.startActivity(intent)
                    },
                    onDelete = { member ->
                        // 🔹 AÇÃO DE DELETAR: Chama o ViewModel para excluir o membro
                        viewModel.delete(member.id)
                        Toast.makeText(context, "${member.name} excluído.", Toast.LENGTH_SHORT).show()
                    },
                    // Implemente ações de clique se quiser que a busca leve a edição/detalhes
                    onMemberClick = { member ->
                        // Lógica para abrir detalhes ou edição do membro clicado
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    members: List<Member>,
    onBack: () -> Unit,
    onEdit: (Member) -> Unit,
    onDelete: (Member) -> Unit,
    onMemberClick: (Member) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buscar Membros") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onQueryChange,
                label = { Text("Digite o nome ou departamento...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Limpar busca")
                        }
                    }
                }
            )

            if (members.isEmpty() && searchQuery.isNotBlank()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhum resultado encontrado para \"$searchQuery\"")
                }
            } else if (members.isEmpty() && searchQuery.isBlank()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Comece a digitar para buscar.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(members, key = { it.id }) { member ->

                        MemberItem(
                            member = member,
                            onEditClick = { onEdit(member) },
                            onDeleteClick = { onDelete(member) }
                        )
                    }
                }
            }
        }
    }
}