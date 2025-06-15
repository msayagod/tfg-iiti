package com.mariosayago.tfg_iiti.view.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mariosayago.tfg_iiti.model.entities.Machine
import com.mariosayago.tfg_iiti.viewmodel.MachineViewModel

@Composable
fun MachineFormScreen(
    machineId: Long? = null, // Añadir parámetro para editar
    onSave: () -> Unit,
    viewModel: MachineViewModel = hiltViewModel()
) {
    // 1) Flow que emite la máquina (o null si es creación)
    val existingMachine by if (machineId != null) {
        viewModel.getMachineById(machineId)
            .collectAsState(initial = null)
    } else {
        // dummy state para un solo valor null
        remember { mutableStateOf<Machine?>(null) }
    }

    // Campos del formulario
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var rows by remember { mutableStateOf("") }
    var columns by remember { mutableStateOf("") }

    // Rango válido para filas/columnas
    val minSize = 1
    val maxSize = 12

    // Valores numéricos y validaciones
    val rowsVal  = rows.toIntOrNull()    ?: -1
    val colsVal  = columns.toIntOrNull() ?: -1
    val rowsValid = rowsVal in minSize..maxSize
    val colsValid = colsVal in minSize..maxSize

    // Form válido: textos no vacíos y números en rango
    val formValid = name.isNotBlank()
            && location.isNotBlank()
            && rowsValid
            && colsValid

    // 3) Cuando existingMachine cambie de null → non-null, rellenamos los campos
    LaunchedEffect(existingMachine) {
        existingMachine?.let { m ->
            name     = m.name
            location = m.location
            rows     = m.rows.toString()
            columns  = m.columns.toString()
        }
    }

    Column(Modifier.padding(16.dp)) {
        // Nombre
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            isError = name.isBlank()
        )
        if (name.isBlank()) {
            Text(
                "El nombre no puede estar vacío",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }

        // Ubicación
        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Ubicación") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            isError = location.isBlank()
        )
        if (location.isBlank()) {
            Text(
                "La ubicación no puede estar vacía",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }

        // Filas
        OutlinedTextField(
            value = rows,
            onValueChange = { input ->
                rows = input.filter { it.isDigit() }
            },
            label = { Text("Filas") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            isError = !rowsValid
        )
        if (!rowsValid) {
            Text(
                "Debe estar entre $minSize y $maxSize",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }

        // Columnas
        OutlinedTextField(
            value = columns,
            onValueChange = { input ->
                columns = input.filter { it.isDigit() }
            },
            label = { Text("Columnas") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            isError = !colsValid
        )
        if (!colsValid) {
            Text(
                "Debe estar entre $minSize y $maxSize",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }

        // Botón
        Button(
            onClick = {
                val r = rowsVal
                val c = colsVal
                val machine = Machine(
                    id       = machineId ?: 0L,
                    name     = name,
                    location = location,
                    rows     = r,
                    columns  = c
                )
                if (machineId == null) {
                    // Aquí usamos tu insertAndSeed con callback
                    viewModel.insertAndSeed(machine) {
                        onSave()
                    }
                } else {
                    viewModel.update(machine)
                    onSave()
                }
            },
            enabled = formValid,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(if (machineId == null) "Crear" else "Guardar cambios")
        }
    }
}


