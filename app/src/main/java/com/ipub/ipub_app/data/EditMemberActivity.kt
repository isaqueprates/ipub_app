package com.ipub.ipub_app

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
// 🔹 Imports para rolagem
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange // Importar ícone de data
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ipub.ipub_app.ui.theme.Ipub_appTheme
import com.ipub.ipub_app.viewmodel.MemberViewModel
import java.util.Calendar

class EditMemberActivity : ComponentActivity() {
    // ... (onCreate existente)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val memberId = intent.getStringExtra("memberId")

        if (memberId == null) {
            Toast.makeText(this, "Erro: membro não encontrado.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setContent {
            Ipub_appTheme {
                EditMemberScreen(
                    memberId = memberId,
                    onBack = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMemberScreen(
    memberId: String,
    onBack: () -> Unit
) {
    val viewModel: MemberViewModel = viewModel()
    val context = LocalContext.current

    val member by viewModel.getMemberById(memberId).collectAsState(initial = null)

    // Estados dos campos, inicializados com 'remember(member)' para atualizar quando o membro carregar
    var name by remember(member) { mutableStateOf(member?.name ?: "") }
    var role by remember(member) { mutableStateOf(member?.role ?: "") }
    var department by remember(member) { mutableStateOf(member?.department ?: "") }
    var birthday by remember(member) { mutableStateOf(member?.birthday ?: "") }
    var dateBatism by remember(member) { mutableStateOf(member?.dateBatism ?: "") }
    var dateEspirit by remember(member) { mutableStateOf(member?.dateEspirit ?: "") }

    var departmentDropdown by remember { mutableStateOf(false) }

    val calendar = Calendar.getInstance()

    // 🔹 DatePickers para cada campo de data
    val datePickerBirthday = DatePickerDialog(context, { _, y, m, d ->
        birthday = String.format("%02d/%02d/%04d", d, m + 1, y)
    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

    val datePickerBatism = DatePickerDialog(context, { _, y, m, d ->
        dateBatism = String.format("%02d/%02d/%04d", d, m + 1, y)
    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

    val datePickerEspirit = DatePickerDialog(context, { _, y, m, d ->
        dateEspirit = String.format("%02d/%02d/%04d", d, m + 1, y)
    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

    if (member == null) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar ${member?.name ?: ""}") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Voltar") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()) // 🔹 Adicionado rolagem
        ) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nome") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(value = role, onValueChange = { role = it }, label = { Text("Cargo") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(value = department, onValueChange = { department = it }, label = { Text("Departamento") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(10.dp))

            // 🔹 CAMPO ANIVERSÁRIO COMPLETO
            OutlinedTextField(
                value = birthday, onValueChange = {}, readOnly = true, label = { Text("Aniversário") },
                trailingIcon = { IconButton(onClick = { datePickerBirthday.show() }) { Icon(Icons.Default.DateRange, contentDescription = null) } },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))

            // 🔹 CAMPO BATISMO ÁGUAS COMPLETO
            OutlinedTextField(
                value = dateBatism, onValueChange = {}, readOnly = true, label = { Text("Batismo nas Águas") },
                trailingIcon = { IconButton(onClick = { datePickerBatism.show() }) { Icon(Icons.Default.DateRange, contentDescription = null) } },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))

            // 🔹 CAMPO BATISMO ESPÍRITO COMPLETO
            OutlinedTextField(
                value = dateEspirit, onValueChange = {}, readOnly = true, label = { Text("Batismo no Espírito") },
                trailingIcon = { IconButton(onClick = { datePickerEspirit.show() }) { Icon(Icons.Default.DateRange, contentDescription = null) } },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(20.dp))


            Button(
                onClick = {
                    if (name.isNotBlank() && role.isNotBlank()) {
                        val updatedMember = member!!.copy(
                            name = name,
                            role = role,
                            department = department,
                            birthday = birthday,
                            dateBatism = dateBatism,
                            dateEspirit = dateEspirit
                        )
                        viewModel.update(updatedMember)
                        Toast.makeText(context, "Membro atualizado!", Toast.LENGTH_SHORT).show()
                        onBack()
                    } else {
                        Toast.makeText(context, "Preencha nome e cargo", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Salvar Alterações")
            }
        }
    }
}