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
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var rows by remember { mutableStateOf("") }
    var columns by remember { mutableStateOf("") }

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
                val r = rows.toIntOrNull() ?: 0
                val c = columns.toIntOrNull() ?: 0
                viewModel.insert(
                    Machine(
                        name = name,
                        location = location,
                        rows = r,
                        columns = c
                    )
                )
                onSave()
            },
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
        ) {
            Text("Guardar")
        }
    }
}
