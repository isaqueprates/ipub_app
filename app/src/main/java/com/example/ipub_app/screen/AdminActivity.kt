package com.example.ipub_app.screen

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.ipub_app.data.FirebaseRepository
import com.example.ipub_app.ui.theme.Ipub_appTheme
import kotlinx.coroutines.launch

class AdminActivity : ComponentActivity() {
    private val repo by lazy { FirebaseRepository(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Ipub_appTheme {
                AdminScreen(repo = repo)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(repo: FirebaseRepository) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val activity = context as? ComponentActivity

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("member") }
    val roles = listOf("member", "admin")

    var users by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

    // 🔹 Escuta atualizações em tempo real no Firestore
    LaunchedEffect(Unit) {
        repo.listenToUsers { list -> users = list }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Painel do Administrador") },
                navigationIcon = {
            IconButton(onClick = {activity?.finish()}) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
            }
        }
        )

        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Criar novo usuário", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nome") })
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("E-mail") })
            OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Senha") })

            var expanded by remember { mutableStateOf(false) }
            Box {
                OutlinedTextField(
                    value = role,
                    onValueChange = {},
                    label = { Text("Função") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Selecionar função")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    roles.forEach { r ->
                        DropdownMenuItem(
                            text = { Text(r) },
                            onClick = {
                                role = r
                                expanded = false
                            }
                        )
                    }
                }
            }

            Button(
                onClick = {
                    scope.launch {
                        if (email.isBlank() || password.isBlank() || name.isBlank()) {
                            Toast.makeText(context, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                            return@launch
                        }

                        val success = repo.createUser(email, password, name, role)
                        Toast.makeText(
                            context,
                            if (success) "Usuário criado com sucesso!" else "Erro ao criar usuário",
                            Toast.LENGTH_SHORT
                        ).show()

                        email = ""
                        password = ""
                        name = ""
                        role = "member"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Criar Usuário")
            }

            Divider(Modifier.padding(vertical = 8.dp))

            Text("Usuários Cadastrados", style = MaterialTheme.typography.titleMedium)

            if (users.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(users) { user ->
                        AdminUserCard(user = user, repo = repo)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminUserCard(user: Map<String, Any>, repo: FirebaseRepository) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val uid = user["uid"] as? String ?: ""
    val email = user["email"] as? String ?: ""
    val role = user["role"] as? String ?: "member"

    var showConfirmDialog by remember { mutableStateOf(false) }

    Card(Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(3.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text(email, style = MaterialTheme.typography.titleSmall)
            Text("Função: $role", style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (role != "admin") {
                    Button(onClick = {
                        scope.launch {
                            repo.updateUserRole(uid, "admin")
                            Toast.makeText(context, "$email agora é admin", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Text("Tornar Admin")
                    }
                } else {
                    OutlinedButton(onClick = {
                        scope.launch {
                            repo.updateUserRole(uid, "member")
                            Toast.makeText(context, "$email agora é membro", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Text("Tornar Membro")
                    }
                }

                OutlinedButton(onClick = { showConfirmDialog = true }) {
                    Text("Excluir")
                }
            }
        }
    }

    // 🔹 Diálogo de confirmação antes da exclusão
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Excluir Usuário") },
            text = { Text("Tem certeza que deseja excluir o usuário $email? Esta ação não pode ser desfeita.") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        repo.deleteUser(uid)
                        Toast.makeText(context, "Usuário excluído!", Toast.LENGTH_SHORT).show()
                        showConfirmDialog = false
                    }
                }) {
                    Text("Sim, excluir", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
