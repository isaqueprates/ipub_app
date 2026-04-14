package com.ipub.ipub_app

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ipub.ipub_app.viewmodel.MemberViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMemberScreen(
    memberId: String,
    onBack: () -> Unit,
    viewModel: MemberViewModel = viewModel()
) {
    val context = LocalContext.current
    val member = viewModel.getMemberById(memberId).collectAsState(initial = null).value

    var name by remember { mutableStateOf("") }
    var birthday by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }

    // Preencher automaticamente quando carregar
    LaunchedEffect(member) {
        member?.let {
            name = it.name
            birthday = it.birthday
            department = it.department
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Membro") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->

        if (member == null) {
            // Mensagem enquanto carrega do Firestore
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nome") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = birthday,
                onValueChange = { birthday = it },
                label = { Text("Data de nascimento") },
                modifier = Modifier.fillMaxWidth()
            )

            // Lista de departamentos
            val departments = listOf(
                "Heróis da Fé",
                "Vencedores Pentecostais",
                "Jardim de Deus",
                "Filhas de Sião",
                "Filhos do Reino"
            )

            var departmentDropdown by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = departmentDropdown,
                onExpandedChange = { departmentDropdown = it }
            ) {
                OutlinedTextField(
                    value = department,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Departamento") },
                    trailingIcon = {
                        Icon(Icons.Default.ArrowDropDown, null)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = departmentDropdown,
                    onDismissRequest = { departmentDropdown = false }
                ) {
                    departments.forEach {
                        DropdownMenuItem(
                            text = { Text(it) },
                            onClick = {
                                department = it
                                departmentDropdown = false
                            }
                        )
                    }
                }
            }


            Button(
                onClick = {
                    viewModel.update(
                        member.copy(
                            name = name,
                            birthday = birthday,
                            department = department
                        )
                    )

                    Toast.makeText(context, "Atualizado com sucesso!", Toast.LENGTH_SHORT).show()

                    onBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Salvar alterações")
            }
        }
    }
}
