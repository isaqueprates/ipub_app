package com.ipub.ipub_app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ipub.ipub_app.viewmodel.MemberViewModel
import com.ipub.ipub_app.ui.theme.PrimaryBlue
import com.ipub.ipub_app.ui.theme.SecondaryPink
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.ipub.ipub_app.data.Member

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: MemberViewModel = viewModel()) {
    val members by viewModel.members.collectAsState()
    val today = LocalDate.now()

    var selectedDate by remember { mutableStateOf(today) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    val daysInMonth = currentMonth.lengthOfMonth()

    // Agrupa aniversários por dia/mês
    val birthdaysByDay = remember(members) {
        members.groupBy { member ->
            val parts = member.birthday.split("/")
            if (parts.size >= 2)
                "${parts[0].padStart(2, '0')}/${parts[1].padStart(2, '0')}"
            else ""
        }
    }

    val selectedDayKey = selectedDate.format(DateTimeFormatter.ofPattern("dd/MM"))
    val birthdayMembers = birthdaysByDay[selectedDayKey] ?: emptyList()

    val fullDate = selectedDate.format(
        DateTimeFormatter.ofPattern(
            "EEEE, dd 'de' MMMM 'de' yyyy",
            Locale("pt", "BR")
        )
    ).replaceFirstChar { it.uppercase() }


    Scaffold(
        topBar = { TopAppBar(title = {  }) }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Data atual
            Text(
                text = fullDate,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    selectedDate = today
                    currentMonth = YearMonth.from(today)
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Cabeçalho de mês
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Mês anterior
                IconButton(onClick = {
                    currentMonth = currentMonth.minusMonths(1)
                    selectedDate = selectedDate
                        .withDayOfMonth(
                            minOf(selectedDate.dayOfMonth, currentMonth.lengthOfMonth())
                        )
                        .withMonth(currentMonth.monthValue)
                }) {
                    Icon(Icons.Default.ArrowBackIos, contentDescription = "Mês anterior")
                }

                val monthYear = currentMonth.format(
                    DateTimeFormatter.ofPattern("MMMM 'de' yyyy", Locale("pt", "BR"))
                ).replaceFirstChar { it.uppercase() }

                Text(
                    text = monthYear,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )

                // Próximo mês
                IconButton(onClick = {
                    currentMonth = currentMonth.plusMonths(1)
                    selectedDate = selectedDate
                        .withDayOfMonth(
                            minOf(selectedDate.dayOfMonth, currentMonth.lengthOfMonth())
                        )
                        .withMonth(currentMonth.monthValue)
                }) {
                    Icon(Icons.Default.ArrowForwardIos, contentDescription = "Próximo mês")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Calendário corrigido
            CalendarGrid(
                daysInMonth = daysInMonth,
                currentMonth = currentMonth,
                birthdaysByDay = birthdaysByDay,
                selectedDate = selectedDate,
                today = today,
                onDayClick = { selectedDate = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Lista de aniversariantes
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
    birthdaysByDay: Map<String, List<Member>>,
    selectedDate: LocalDate,
    today: LocalDate,
    onDayClick: (LocalDate) -> Unit
) {
    val firstDay = LocalDate.of(currentMonth.year, currentMonth.month, 1)
    val offset = firstDay.dayOfWeek.value %7 // domingo = 0
    val totalCells = offset + daysInMonth

    // Completa a última linha com null para manter 7 colunas sempre
    val filledDays = buildList<Int?> {
        repeat(offset) { add(null) }
        for (i in 1..daysInMonth) add(i)
        while (size % 7 != 0) add(null)  // <-- garante alinhamento das linhas
    }

    val weeks = filledDays.chunked(7)

    Column {
        WeekDayHeader()

        weeks.forEach { week ->
            Row(Modifier.fillMaxWidth()) {

                week.forEach { day ->
                    val date = day?.let { LocalDate.of(currentMonth.year, currentMonth.month, it) }
                    val key = day?.let { "%02d/%02d".format(it, currentMonth.monthValue) }
                    val hasBirthday = key != null && birthdaysByDay.containsKey(key)
                    val isSelected = date == selectedDate
                    val isToday = date == today

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .padding(4.dp)
                            .background(
                                when {
                                    isSelected -> Color(0xFF007398)
                                    isToday -> Color(0xFF007398)
                                    hasBirthday -> SecondaryPink
                                    else -> Color(0xFFEFEFEF)
                                },
                                shape = MaterialTheme.shapes.small
                            )
                            .clickable(enabled = day != null) {
                                if (date != null) onDayClick(date)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (day != null) {
                            Text(
                                text = day.toString(),
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground,
                                fontWeight = if (isSelected || hasBirthday) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}


@Composable
fun WeekDayHeader() {
    val weekDays = listOf("D", "S", "T", "Q", "Q", "S", "S")

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        weekDays.forEach { day ->
            Box(
                modifier = Modifier
                    .weight(1f)          // <-- MESMO TAMANHO DAS CÉLULAS
                    .aspectRatio(1f),    // <-- quadrado
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(6.dp))
}

