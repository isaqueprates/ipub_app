import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ipub.ipub_app.data.Member
import com.ipub.ipub_app.data.MemberItem

@Composable
fun MembersScreen(
    members: List<Member>,
    onAddClick: () -> Unit,
    onEdit: (Member) -> Unit,
    onDelete: (Member) -> Unit,
    onSearchClick: () -> Unit,
    innerPadding: PaddingValues
) {
    var memberToDelete by remember { mutableStateOf<Member?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar membro",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(padding)
                .fillMaxSize()
        ) {
            // 🔹 Contador de membros e Botão de Busca na mesma linha
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween, // Espaça os elementos
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (members.isNotEmpty()) {
                    Text(
                        text = "Total de membros: ${members.size}",
                        style = MaterialTheme.typography.titleMedium,
                    )
                }

                // 🔹 BOTÃO DE BUSCA AQUI
                IconButton(onClick = onSearchClick) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar membros",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }


            if (members.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Nenhum membro cadastrado.")
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
                            onDeleteClick = { memberToDelete = member }
                        )
                    }
                }
            }



            // Snackbar de confirmação
            LaunchedEffect(memberToDelete) {
                memberToDelete?.let { member ->

                    val result = snackbarHostState.showSnackbar(
                        message = "Excluir ${member.name}?",
                        actionLabel = "Sim",
                        withDismissAction = true
                    )

                    when (result) {
                        SnackbarResult.ActionPerformed -> onDelete(member)
                        SnackbarResult.Dismissed -> snackbarHostState.showSnackbar("Cancelado.")
                    }

                    memberToDelete = null
                }
            }
        }
    }
}
