package com.mariosayago.tfg_iiti.view.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
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
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Ubicación") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
        // Filas: solo digitos
        OutlinedTextField(
            value = rows,
            onValueChange = { input ->
                // filtrar cualquier carácter que no sea un dígitp
                rows = input.filter { it.isDigit() }
            },
            label = { Text("Filas") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), // Solo números
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
        // Columnas: solo enteros
        OutlinedTextField(
            value = columns,
            onValueChange = { input ->
                columns = input.filter { it.isDigit() }
            },
            label = { Text("Columnas") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), // Solo números
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
        Button(
            onClick = {
                val r = rows.toIntOrNull()    ?: 0
                val c = columns.toIntOrNull() ?: 0
                val machine = Machine(
                    id       = machineId ?: 0L,
                    name     = name,
                    location = location,
                    rows     = r,
                    columns  = c
                )
                if (machineId == null) {
                    viewModel.insert(machine)
                } else {
                    viewModel.update(machine)
                }
                onSave()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(if (machineId == null) "Crear" else "Guardar cambios")
        }
    }
}
