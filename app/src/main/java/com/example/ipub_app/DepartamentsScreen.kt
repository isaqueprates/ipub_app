package com.example.ipub_app

import android.content.Intent
import androidx.activity.ComponentActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.ipub_app.ui.theme.Ipub_appTheme
import com.example.ipub_app.viewmodel.MemberViewModel

class DepartmentMembersActivity : ComponentActivity() {

    private val viewModel: MemberViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Ipub_appTheme {
                DepartmentMembersScreen(
                    viewModel = viewModel,
                    onBack = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepartmentMembersScreen(
    viewModel: MemberViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    val departments = listOf(
        "Heróis da Fé",
        "Vencedores Pentecostais",
        "Jardim de Deus",
        "Filhas de Sião",
        "Filhos do Reino"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Departamentos") }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Selecione um departamento:",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 Lista de departamentos
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(departments) { department ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // 👉 Abre a tela MembersGroupActivity passando o nome do departamento
                                val intent = Intent(context, MembersGroupActivity::class.java)
                                intent.putExtra("group_name", department)
                                context.startActivity(intent)
                            },
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = department,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
