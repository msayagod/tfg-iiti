package com.mariosayago.tfg_iiti.view.screens

import android.app.DatePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import com.mariosayago.tfg_iiti.model.entities.VisitSchedule
import com.mariosayago.tfg_iiti.viewmodel.MachineViewModel
import com.mariosayago.tfg_iiti.viewmodel.VisitScheduleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScheduleFormScreen(
    initialScheduleId: Long? = null,
    onDone: () -> Unit,
    viewModel: VisitScheduleViewModel = hiltViewModel(),
    machineVm: MachineViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // 1. Carga máquinas y estados
    val machines by machineVm.machines.collectAsState()
    var selectedMachine by rememberSaveable { mutableStateOf<Long?>(null) }

    var date by rememberSaveable { mutableStateOf<LocalDate?>(null) }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    var frequency by rememberSaveable { mutableStateOf("d") }
    var daysOfWeek by rememberSaveable { mutableStateOf(listOf<Int>()) }
    var dayOfMonth by rememberSaveable { mutableIntStateOf(1) }

    var showOverwriteDialog by rememberSaveable { mutableStateOf(false) }
    var pendingSchedule by remember { mutableStateOf<VisitSchedule?>(null) }

    Column(
        Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Máquina
        Text("Máquina:", style = MaterialTheme.typography.titleMedium)
        machines.forEach { m ->
            val isSelected = m.id == selectedMachine
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable { selectedMachine = m.id }
                    .background(
                        if (isSelected)
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        else Color.Transparent
                    )
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = isSelected,
                    onClick = { selectedMachine = m.id }
                )
                Text(m.name, Modifier.padding(start = 8.dp))
            }
        }

        Spacer(Modifier.height(16.dp))

        // Fecha
        Text("Programar un día:", style = MaterialTheme.typography.titleMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = date?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    ?: "Seleccionar fecha",
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { showDatePicker = true }) {
                Icon(Icons.Default.CalendarToday, contentDescription = "Abrir picker")
            }
            // Si hay fecha, mostramos la X para limpiar
            if (date != null) {
                IconButton(onClick = { date = null }) {
                    Icon(Icons.Default.Close, contentDescription = "Limpiar fecha")
                }
            }
        }


        Spacer(Modifier.height(16.dp))

        // Frecuencia
        Text("Frecuencia:", style = MaterialTheme.typography.titleMedium)
        val freqOptions = listOf("Diaria" to "d", "Semanal" to "w", "Mensual" to "m")
        freqOptions.forEach { (label, code) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = frequency == code,
                    onClick = {
                        frequency = code
                        // Al elegir frecuencia, borramos la fecha
                        date = null
                    },
                    enabled = date == null  //Deshabilitado si hay fecha
                )
                Text(
                    label,
                    Modifier
                        .clickable (enabled = date == null) {  //Deshabilitado si hay fecha
                            frequency = code
                            date = null}  // Borramos la fecha
                        .padding(start = 8.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Días según frecuencia
        when (frequency) {
            "w" -> {
                Text("Días de la semana:", style = MaterialTheme.typography.titleMedium)
                DayOfWeek.entries.forEach { dow ->
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val v = dow.value
                        Checkbox(
                            checked = daysOfWeek.contains(v),
                            onCheckedChange = {
                                daysOfWeek = if (it)
                                    daysOfWeek + v
                                else
                                    daysOfWeek - v
                            }
                        )
                        Text(dow.name, Modifier.padding(start = 8.dp))
                    }
                }
            }
            "m" -> {
                Text("Día del mes:", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = dayOfMonth.toString(),
                    onValueChange = { input ->
                        input.toIntOrNull()?.let { d ->
                            if (d in 1..31) dayOfMonth = d
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Botón guardar
        Button(
            onClick = {
                selectedMachine?.let { mid ->
                    val s = VisitSchedule(
                        id = initialScheduleId ?: 0L,
                        machineId = mid,
                        frequency = frequency,
                        daysOfWeek = daysOfWeek.joinToString(","),
                        dayOfMonth = if (frequency == "m") dayOfMonth else null
                    )
                    coroutineScope.launch {
                        val existing = viewModel.scheduleRepo.getSchedulesByMachine(mid)
                        if (existing.isNotEmpty() && initialScheduleId == null) {
                            pendingSchedule = s
                            showOverwriteDialog = true
                        } else {
                            viewModel.scheduleRepo.insertSchedule(s)
                            onDone()
                        }
                    }
                }
            },
            enabled = selectedMachine != null
        ) {
            Text(if (initialScheduleId == null) "Programar" else "Actualizar")
        }
    }

    // Diálogo de sobrescribir
    if (showOverwriteDialog) {
        AlertDialog(
            onDismissRequest = { showOverwriteDialog = false },
            title = { Text("Sobrescribir programación") },
            text = { Text("Ya existe una programación para esta máquina. ¿Deseas sobrescribirla?") },
            confirmButton = {
                TextButton(onClick = {
                    coroutineScope.launch {
                        selectedMachine?.let { mid ->
                            viewModel.scheduleRepo.deleteByMachine(mid)
                        }
                        pendingSchedule?.let {
                            viewModel.scheduleRepo.insertSchedule(it)
                        }
                        showOverwriteDialog = false
                        onDone()
                    }
                }) { Text("Sí") }
            },
            dismissButton = {
                TextButton(onClick = { showOverwriteDialog = false }) { Text("No") }
            }
        )
    }

    // DatePicker nativo
    if (showDatePicker) {
        val today = LocalDate.now()
        DatePickerDialog(
            context,
            { _, year, month, day ->
                date = LocalDate.of(year, month + 1, day)
                frequency = "x" //vale cualquier cosa para que salga el default (en el viewmodel)
                showDatePicker = false
            },
            today.year,
            today.monthValue - 1,
            today.dayOfMonth
        ).show()
    }
}


