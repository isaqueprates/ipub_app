package com.ipub.ipub_app.data

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MemberItem(
    member: Member,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(modifier = Modifier.weight(1f)) {
                Text(member.name, style = MaterialTheme.typography.titleMedium)
                Text(member.role, style = MaterialTheme.typography.bodyMedium)

                if (member.department.isNotEmpty()) {
                    Text(
                        "Depto: ${member.department}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (member.birthday.isNotEmpty()) {
                    Text(
                        "Aniversário: ${member.birthday}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Text(
                    "Data de Batismo: ${member.dateBatism}",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    "Batismo com espirito: ${member.dateEspirit}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            IconButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, contentDescription = "Editar")
            }

            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, contentDescription = "Excluir")
            }
        }
    }
}
