package com.ipub.ipub_app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ipub.ipub_app.viewmodel.MemberViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirthdayDetailScreen(
    selectedDate: LocalDate,
    viewModel: MemberViewModel,
    onBackClick: () -> Unit
) {
    val members by viewModel.members.collectAsState()

    // Formata a chave para buscar na lista (ex: "25/10")
    val dateFormat = "dd/MM"
    val selectedDayKey = selectedDate.format(DateTimeFormatter.ofPattern(dateFormat))

    // Filtra os aniversariantes do dia
    val birthdayMembers = remember(members, selectedDate) {
        members.filter { member ->
            val parts = member.birthday.split("/")
            if (parts.size >= 2) {
                val memberKey = "${parts[0].padStart(2, '0')}/${parts[1].padStart(2, '0')}"
                memberKey == selectedDayKey
            } else false
        }
    }

    val formattedDateTitle = selectedDate.format(
        DateTimeFormatter.ofPattern("dd 'de' MMMM", Locale("pt", "BR"))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Aniversariantes de $formattedDateTitle") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar ao Calendário"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (birthdayMembers.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nenhum aniversário cadastrado para esta data.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Text(
                    text = "Total: ${birthdayMembers.size} aniversariante(s)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(birthdayMembers) { member ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(3.dp)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(
                                    text = member.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Departamento: ${member.department}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
