package com.mariosayago.tfg_iiti.view.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mariosayago.tfg_iiti.viewmodel.MachineViewModel
import java.time.LocalDate
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.*
import java.time.Instant
import java.time.ZoneId


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportFilterScreen(
    onGenerate: (Long, String, String) -> Unit,
    machineVm: MachineViewModel = hiltViewModel()
) {
    val machines by machineVm.machines.collectAsState(initial = emptyList())
    var selectedId by remember { mutableStateOf<Long?>(machines.firstOrNull()?.id) }
    var expanded by remember { mutableStateOf(false) }
    val selectedName = machines.firstOrNull { it.id == selectedId }?.name ?: ""

    // 1) Estados para los pickers
    var fromDate by rememberSaveable { mutableStateOf(LocalDate.now()) }
    var toDate by rememberSaveable { mutableStateOf(LocalDate.now()) }
    var showFromPicker by remember { mutableStateOf(false) }
    var showToPicker by remember { mutableStateOf(false) }

    // 2) Dos estados independientes
    val fromPickerState = rememberDatePickerState(
        initialSelectedDateMillis = fromDate
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant().toEpochMilli()
    )
    val toPickerState = rememberDatePickerState(
        initialSelectedDateMillis = toDate
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant().toEpochMilli()
    )

    Column(
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // — Dropdown de máquina —
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = selectedName,
                onValueChange = {},
                readOnly = true,
                label = { Text("Máquina") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(
                        type = MenuAnchorType.PrimaryNotEditable,
                        enabled = true
                    )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                machines.forEach { m ->
                    DropdownMenuItem(
                        text = { Text(m.name) },
                        onClick = {
                            selectedId = m.id
                            expanded = false
                        }
                    )
                }
            }
        }

        // — Campo “Desde” —
        OutlinedTextField(
            value = fromDate.toString(),
            onValueChange = {},
            readOnly = true,
            label = { Text("Desde") },
            trailingIcon = {
                IconButton(onClick = { showFromPicker = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha Desde")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        if (showFromPicker) {
            DatePickerDialog(
                onDismissRequest = { showFromPicker = false },
                confirmButton = {
                    TextButton({
                        fromPickerState.selectedDateMillis?.let { ms ->
                            fromDate = Instant.ofEpochMilli(ms)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        }
                        showFromPicker = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton({ showFromPicker = false }) { Text("Cancelar") }
                }
            ) {
                DatePicker(state = fromPickerState)
            }
        }

        // — Campo “Hasta” —
        OutlinedTextField(
            value = toDate.toString(),
            onValueChange = {},
            readOnly = true,
            label = { Text("Hasta") },
            trailingIcon = {
                IconButton(onClick = { showToPicker = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha Hasta")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        if (showToPicker) {
            DatePickerDialog(
                onDismissRequest = { showToPicker = false },
                confirmButton = {
                    TextButton({
                        toPickerState.selectedDateMillis?.let { ms ->
                            toDate = Instant.ofEpochMilli(ms)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        }
                        showToPicker = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton({ showToPicker = false }) { Text("Cancelar") }
                }
            ) {
                DatePicker(state = toPickerState)
            }
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                selectedId?.let {
                    onGenerate(it, fromDate.toString(), toDate.toString())
                }
            },
            enabled = selectedId != null && fromDate <= toDate,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Generar informe")
        }
    }
}



