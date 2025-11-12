package com.example.ipub_app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ipub_app.viewmodel.MemberViewModel
import com.example.ipub_app.ui.theme.PrimaryBlue
import com.example.ipub_app.ui.theme.SecondaryPink
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: MemberViewModel = viewModel()) {
    val members by viewModel.members.collectAsState()
    val today = LocalDate.now()

    var selectedDate by remember { mutableStateOf(today) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    val daysInMonth = currentMonth.lengthOfMonth()

    // 🔹 Agrupa aniversários por dia/mês
    val birthdaysByDay = remember(members) {
        members.groupBy { member ->
            val parts = member.birthday.split("/")
            if (parts.size >= 2) "${parts[0].padStart(2, '0')}/${parts[1].padStart(2, '0')}" else ""
        }
    }

    val selectedDayKey = selectedDate.format(DateTimeFormatter.ofPattern("dd/MM"))
    val birthdayMembers = birthdaysByDay[selectedDayKey] ?: emptyList()

    val fullDate = today.format(
        DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM 'de' yyyy", Locale("pt", "BR"))
    ).replaceFirstChar { it.uppercase() }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Aniversariantes 🎉") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🔹 Data atual
            Text(
                text = fullDate,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 🔹 Cabeçalho com setas para trocar mês
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    currentMonth = currentMonth.minusMonths(1)
                    selectedDate = selectedDate.withMonth(currentMonth.monthValue)
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIos,
                        contentDescription = "Mês anterior",
                        tint = PrimaryBlue
                    )
                }

                val monthYear = currentMonth.format(
                    DateTimeFormatter.ofPattern("MMMM 'de' yyyy", Locale("pt", "BR"))
                ).replaceFirstChar { it.uppercase() }

                Text(
                    text = monthYear,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )

                IconButton(onClick = {
                    currentMonth = currentMonth.plusMonths(1)
                    selectedDate = selectedDate.withMonth(currentMonth.monthValue)
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowForwardIos,
                        contentDescription = "Próximo mês",
                        tint = PrimaryBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 🔹 Calendário
            CalendarGrid(
                daysInMonth = daysInMonth,
                currentMonth = currentMonth,
                birthdaysByDay = birthdaysByDay,
                selectedDate = selectedDate,
                onDayClick = { selectedDate = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 🔹 Lista de aniversariantes
            if (birthdayMembers.isEmpty()) {
                Text(
                    text = "Nenhum aniversário em ${selectedDate.format(DateTimeFormatter.ofPattern("dd/MM"))}",
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                Text(
                    text = "Aniversariantes de ${selectedDate.format(DateTimeFormatter.ofPattern("dd/MM"))}:",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(birthdayMembers) { member ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(3.dp)
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text(member.name, style = MaterialTheme.typography.titleMedium)
                                Text(member.department)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarGrid(
    daysInMonth: Int,
    currentMonth: YearMonth,
    birthdaysByDay: Map<String, List<*>>,
    selectedDate: LocalDate,
    onDayClick: (LocalDate) -> Unit
) {
    val rows = (1..daysInMonth).chunked(7)
    val firstDayOfMonth = LocalDate.of(currentMonth.year, currentMonth.month, 1)
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
    val daysList = mutableListOf<Int?>()
    repeat(firstDayOfWeek) { daysList.add(null) }
    (1..daysInMonth).forEach { daysList.add(it) }
    val weeks = daysList.chunked(7)

    Column {
        // 🆕 1. Adiciona a linha dos dias da semana
        WeekDayHeader()

        // 🆕 2. Itera sobre as semanas e dias
        weeks.forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                week.forEach { day ->
                    if (day != null) {
                        // Se for um dia válido (não null)
                        val date = LocalDate.of(currentMonth.year, currentMonth.month, day)
                        val key = date.format(DateTimeFormatter.ofPattern("dd/MM"))
                        val hasBirthday = birthdaysByDay.containsKey(key)
                        val isSelected = date == selectedDate

                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clickable { onDayClick(date) }
                                .background(
                                    when {
                                        isSelected -> PrimaryBlue
                                        hasBirthday -> SecondaryPink
                                        else -> Color.Transparent
                                    },
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day.toString(),
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground,
                                fontWeight = if (hasBirthday) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    } else {
                        // Se for null (espaço vazio de preenchimento)
                        Spacer(modifier = Modifier.size(44.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

//    Column {
//        rows.forEach { week ->
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceEvenly
//            ) {
//                week.forEach { day ->
//                    val date = LocalDate.of(currentMonth.year, currentMonth.month, day)
//                    val key = date.format(DateTimeFormatter.ofPattern("dd/MM"))
//                    val hasBirthday = birthdaysByDay.containsKey(key)
//                    val isSelected = date == selectedDate
//
//                    Box(
//                        modifier = Modifier
//                            .size(44.dp)
//                            .clickable { onDayClick(date) }
//                            .background(
//                                when {
//                                    isSelected -> PrimaryBlue
//                                    hasBirthday -> SecondaryPink
//                                    else -> Color.Transparent
//                                },
//                                shape = CircleShape
//                            ),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Text(
//                            text = day.toString(),
//                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground,
//                            fontWeight = if (hasBirthday) FontWeight.Bold else FontWeight.Normal
//                        )
//                    }
//                }
//            }
//            Spacer(modifier = Modifier.height(8.dp))
//        }
//    }
//}

// 🆕 Função para exibir os dias da semana
@Composable
fun WeekDayHeader() {
    val weekDays = listOf("D", "S", "T", "Q", "Q", "S", "S")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        weekDays.forEach { day ->
            // Use o mesmo tamanho dos dias para alinhamento perfeito
            Box(
                modifier = Modifier.size(44.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = PrimaryBlue, // Cor de destaque para os dias da semana
                    textAlign = TextAlign.Center
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}
