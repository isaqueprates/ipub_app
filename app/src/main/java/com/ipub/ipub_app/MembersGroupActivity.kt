package com.ipub.ipub_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ipub.ipub_app.ui.theme.Ipub_appTheme
import com.ipub.ipub_app.ui.theme.AppScaffold // Importa o AppScaffold padronizado
import com.ipub.ipub_app.viewmodel.MemberViewModel
import com.ipub.ipub_app.data.Member

class MembersGroupActivity : ComponentActivity() {

    private val viewModel: MemberViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val departmentName = intent.getStringExtra("group_name") ?: "groupactivity"

        setContent {
            Ipub_appTheme {
                val members by viewModel.getMembersByDepartment(departmentName).collectAsState(initial = emptyList())

                DepartmentMembersGroup(
                    departmentName = departmentName,
                    members = members,
                    onBack = { finish() }
                )
            }
        }
    }
}

@Composable
fun DepartmentMembersGroup(
    departmentName: String,
    members: List<Member>,
    onBack: () -> Unit
) {
    // Utilizando o AppScaffold para padronizar a Toolbar com botão de voltar
    AppScaffold(
        title = departmentName,
        showBackButton = true,
        onBackClick = onBack
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Adiciona o texto com a contagem de membros
            Text(
                text = "Total de membros: ${members.size}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
            )

            if (members.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text("Nenhum membro encontrado neste departamento.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(members) { member ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(3.dp)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(member.name, style = MaterialTheme.typography.titleMedium)
                                if (member.role.isNotBlank()) {
                                    Text(member.role, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}