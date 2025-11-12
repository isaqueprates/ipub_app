package com.example.ipub_app

import android.app.Activity
import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.ipub_app.ui.theme.Ipub_appTheme
import com.example.ipub_app.viewmodel.MemberViewModel
import java.util.*

class AddMemberActivity : ComponentActivity() {

    private val viewModel: MemberViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Ipub_appTheme {
                AddMemberScreen(
                    onSave = { name, role, department, birthday ->
                        viewModel.insert(name, role, department, birthday) { success, message ->
                            Toast.makeText(
                                this,
                                message ?: if (success) "Membro criado!" else "Erro ao criar membro",
                                Toast.LENGTH_SHORT
                            ).show()

                            if (success) finish()
                        }
                    },
                    onCancel = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMemberScreen(
    onSave: (String, String, String, String) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as Activity

    // Estados
    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    var birthday by remember { mutableStateOf("") }

    // Lista de departamentos
    val departments = listOf(
        "Heróis da Fé",
        "Vencedores Pentecostais",
        "Jardim de Deus",
        "Filhas de Sião",
        "Filhos do Reino"
    )

    var departmentDropdown by remember { mutableStateOf(false) }

    // DatePicker
    val calendar = Calendar.getInstance()
    val datePicker = DatePickerDialog(
        context,
        { _, y, m, d ->
            birthday = String.format("%02d/%02d/%04d", d, m + 1, y)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Novo membro") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
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
                .fillMaxWidth()
        ) {

            // Nome
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nome") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))

            // Cargo
            OutlinedTextField(
                value = role,
                onValueChange = { role = it },
                label = { Text("Cargo") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))

            // Departamento (Dropdown)
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

            Spacer(Modifier.height(10.dp))

            // Aniversário
            OutlinedTextField(
                value = birthday,
                onValueChange = {},
                readOnly = true,
                label = { Text("Aniversário (DD/MM/AAAA)") },
                trailingIcon = {
                    IconButton(onClick = { datePicker.show() }) {
                        Icon(Icons.Default.DateRange, contentDescription = null)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(Modifier.height(20.dp))

            // Botão salvar
            Button(
                onClick = {
                    if (name.isNotBlank() && role.isNotBlank()) {
                        onSave(name, role, department, birthday)
                    } else {
                        Toast.makeText(context, "Preencha o nome e cargo", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Salvar")
            }
        }
    }
}
