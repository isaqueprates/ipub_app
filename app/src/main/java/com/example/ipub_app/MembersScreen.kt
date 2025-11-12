package com.example.ipub_app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ipub_app.data.Member
import com.example.ipub_app.data.MemberItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembersScreen(
    members: List<Member>,
    onAddClick: () -> Unit,
    onDelete: (Member) -> Unit
) {
    var memberToDelete by remember { mutableStateOf<Member?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Membros (${members.size})") },
                actions = {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = "Ícone de grupo",
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar membro")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        if (members.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Nenhum membro cadastrado.")
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .fillMaxSize()
            ) {
                items(
                    items = members,
                    key = { it.id }
                ) { member ->
                    MemberItem(
                        member = member,
                        onDeleteClick = { memberToDelete = member }
                    )
                }
            }
        }

        // Snackbar de confirmação
        memberToDelete?.let { member ->
            LaunchedEffect(member) {
                val result = snackbarHostState.showSnackbar(
                    message = "Excluir ${member.name}?",
                    actionLabel = "Sim"
                )
                if (result == SnackbarResult.ActionPerformed) {
                    onDelete(member)
                }
                memberToDelete = null
            }
        }
    }
}
