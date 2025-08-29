package com.mariosayago.tfg_iiti.view.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mariosayago.tfg_iiti.viewmodel.VisitScheduleViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Repeat


// UI elements & theming
import androidx.compose.material3.*
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.saveable.rememberSaveable


import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.mariosayago.tfg_iiti.model.CalendarVisit
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun VisitScheduleScreen(
    onScheduleClick: (Long) -> Unit = {},
    onNewSchedule: () -> Unit = {},
    viewModel: VisitScheduleViewModel = hiltViewModel()
) {

    val currentMonth = YearMonth.now()
    val visits by produceState<List<CalendarVisit>>(initialValue = emptyList()) {
        value = viewModel.getCalendarVisitsForMonth(currentMonth.year, currentMonth.monthValue)
    }

    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    val tabTitles = listOf("Hoy", "Semana", "Mes", "Calendario")

    val filteredVisits = when (selectedTab) {
        0 -> visits.filter { it.date == LocalDate.now() }
        1 -> {
            val today = LocalDate.now()
            val endOfWeek = today.with(DayOfWeek.SUNDAY)
            visits.filter { it.date in today..endOfWeek }
        }
        2 -> {
            val today = LocalDate.now()
            val endOfMonth = today.withDayOfMonth(today.lengthOfMonth())
            visits.filter { it.date in today..endOfMonth }
        }
        else -> visits
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onNewSchedule) {
                Icon(Icons.Default.EditCalendar, contentDescription = "Programar visita")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {

            // 🔼 Tabs
            TabRow(selectedTabIndex = selectedTab) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            // 🔽 Lista de visitas filtradas
            if (selectedTab < 3) {
                if (filteredVisits.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay visitas programadas.")
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(filteredVisits) { visit ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .clickable { onScheduleClick(visit.machineId) }
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    visit.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                    modifier = Modifier.weight(1f)
                                )
                                Text(visit.machineName)
                                if (visit.isRecurring) {
                                    Icon(Icons.Default.Repeat, contentDescription = "Recurrente")
                                }
                            }
                            HorizontalDivider()
                        }
                    }
                }
            } else {
                CalendarViewWithVisits(visits)
            }

        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarViewWithVisits(
    visits: List<CalendarVisit>,
) {
    val today = LocalDate.now()
    var currentMonth by remember { mutableStateOf(YearMonth.from(today)) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) } // Estado para la fecha seleccionada

    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfMonth = currentMonth.atDay(1)
    val startDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // 0=Domingo, 6=Sábado

    val calendarDays = buildList {
        repeat(startDayOfWeek) { add(null) } // Días vacíos al principio
        for (day in 1..daysInMonth) {
            add(currentMonth.atDay(day))
        }
    }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Mes anterior")
            }
            Text(
                currentMonth.month.getDisplayName(TextStyle.FULL, Locale("es"))
                    .replaceFirstChar { it.uppercase() } + " ${currentMonth.year}",
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Mes siguiente")
            }
        }

        Spacer(Modifier.height(8.dp))

        // Semana
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            listOf("D", "L", "M", "X", "J", "V", "S").forEach {
                Text(it, Modifier.weight(1f), textAlign = TextAlign.Center)
            }
        }

        Spacer(Modifier.height(4.dp))

        // Celdas del calendario con mejoras visuales y click
        calendarDays.chunked(7).forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { day ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp)
                            .clickable(enabled = day != null) {
                                selectedDate = day
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        day?.let {
                            val isToday = it == today
                            val hasVisit = visits.any { v -> v.date == it }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(
                                            color = if (isToday) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                            else Color.Transparent,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = it.dayOfMonth.toString(),
                                        color = if (isToday) MaterialTheme.colorScheme.primary else LocalContentColor.current,
                                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                                    )
                                }

                                if (hasVisit) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                                    )
                                } else {
                                    Spacer(modifier = Modifier.height(10.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ——— Diálogo de detalles del día ———
    selectedDate?.let { date ->
        val visitsOnDay = visits.filter { it.date == date }
        AlertDialog(
            onDismissRequest = { selectedDate = null },
            title = {
                Text("Visitas del ${date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}")
            },
            text = {
                if (visitsOnDay.isEmpty()) {
                    Text("No hay visitas programadas/enregistradas.")
                } else {
                    LazyColumn {
                        items(visitsOnDay) { visit ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(visit.machineName, Modifier.weight(1f))
                                if (visit.isRecurring) {
                                    Icon(Icons.Default.Repeat, contentDescription = "Recurrente")
                                } else {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Realizada")
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedDate = null }) {
                    Text("Cerrar")
                }
            }
        )
    }
}

