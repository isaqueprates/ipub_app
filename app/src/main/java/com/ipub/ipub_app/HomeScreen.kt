package com.ipub.ipub_app

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ipub.ipub_app.viewmodel.MemberViewModel
import com.ipub.ipub_app.ui.theme.PrimaryBlue
import com.ipub.ipub_app.ui.theme.SecondaryPink
import com.ipub.ipub_app.ui.theme.AppScaffold
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.ipub.ipub_app.data.Member

@Composable
fun HomeScreen(
    viewModel: MemberViewModel = viewModel(),
    onNavigateToBirthdays: (LocalDate) -> Unit = {}
) {
    val members by viewModel.members.collectAsState()
    val today = LocalDate.now()

    var selectedDate by remember { mutableStateOf(today) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    val daysInMonth = currentMonth.lengthOfMonth()
    val context = LocalContext.current

    // Agrupa aniversários por dia/mês
    val birthdaysByDay = remember(members) {
        members.groupBy { member ->
            val parts = member.birthday.split("/")
            if (parts.size >= 2)
                "${parts[0].padStart(2, '0')}/${parts[1].padStart(2, '0')}"
            else ""
        }
    }

    val fullDate = selectedDate.format(
        DateTimeFormatter.ofPattern(
            "EEEE, dd 'de' MMMM 'de' yyyy",
            Locale("pt", "BR")
        )
    ).replaceFirstChar { it.uppercase() }

    // Diálogo para escolher Mês e Ano rapidamente
    fun showMonthYearPickerDialog() {
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, _ ->
                val newYearMonth = YearMonth.of(year, month + 1)
                currentMonth = newYearMonth
                val targetDay = minOf(selectedDate.dayOfMonth, newYearMonth.lengthOfMonth())
                selectedDate = newYearMonth.atDay(targetDay)
            },
            currentMonth.year,
            currentMonth.monthValue - 1,
            1
        )
        datePickerDialog.show()
    }

    AppScaffold(
        title = "Calendário de Aniversários",
        showBackButton = false,
        floatingActionButton = {
            // Botão flutuante mostrando o número do dia de hoje
            FloatingActionButton(
                onClick = {
                    selectedDate = today
                    currentMonth = YearMonth.from(today)
                },
                containerColor = PrimaryBlue,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Box(
                    modifier = Modifier.size(56.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = today.dayOfMonth.toString(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Data atual clicável para voltar ao hoje
            Text(
                text = fullDate,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    selectedDate = today
                    currentMonth = YearMonth.from(today)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Cabeçalho de mês
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mês anterior (apenas muda o mês visualmente)
                IconButton(onClick = {
                    currentMonth = currentMonth.minusMonths(1)
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBackIos, contentDescription = "Mês anterior")
                }

                val monthYear = currentMonth.format(
                    DateTimeFormatter.ofPattern("MMMM 'de' yyyy", Locale("pt", "BR"))
                ).replaceFirstChar { it.uppercase() }

                // Texto do mês/ano clicável para abrir o seletor
                Text(
                    text = monthYear,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clickable { showMonthYearPickerDialog() }
                        .padding(8.dp)
                )

                // Próximo mês (apenas muda o mês visualmente)
                IconButton(onClick = {
                    currentMonth = currentMonth.plusMonths(1)
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = "Próximo mês")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Calendário
            CalendarGrid(
                daysInMonth = daysInMonth,
                currentMonth = currentMonth,
                birthdaysByDay = birthdaysByDay,
                selectedDate = selectedDate,
                today = today,
                onDayClick = { clickedDate ->
                    selectedDate = clickedDate
                    onNavigateToBirthdays(clickedDate)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Dica orientando o usuário
            Text(
                text = "Toque em qualquer dia para ver os aniversariantes",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
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
    val offset = firstDay.dayOfWeek.value % 7 // domingo = 0

    val filledDays = buildList<Int?> {
        repeat(offset) { add(null) }
        for (i in 1..daysInMonth) add(i)
        while (size % 7 != 0) add(null)
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

                    // Validação completa para o dia de hoje real (Ano, Mês e Dia)
                    val isToday = date != null &&
                            date.year == today.year &&
                            date.month == today.month &&
                            date.dayOfMonth == today.dayOfMonth

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .padding(4.dp)
                            .background(
                                when {
                                    isSelected -> Color(0xFF5946F5) // Cor do dia selecionado (Rosa/Destaque)
                                    isToday -> Color(0xFF007398)      // Cor para o dia de hoje real
                                    hasBirthday -> SecondaryPink    // Cor para dias com aniversariantes
                                    else -> Color(0xFFEFEFEF)       // Cor padrão
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
                                color = if (isSelected || isToday) Color.White else MaterialTheme.colorScheme.onBackground,
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
                    .weight(1f)
                    .aspectRatio(1f),
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