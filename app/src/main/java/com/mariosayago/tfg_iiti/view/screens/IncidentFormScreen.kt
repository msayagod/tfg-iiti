package com.mariosayago.tfg_iiti.view.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mariosayago.tfg_iiti.model.entities.Incident
import com.mariosayago.tfg_iiti.viewmodel.IncidentViewModel

@Composable
fun IncidentFormScreen(
    machineId: Long,
    onSave: () -> Unit,
    viewModel: IncidentViewModel = hiltViewModel()
) {
    var date by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var obs  by remember { mutableStateOf("") }

    Column(Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            label = { Text("Fecha (ISO)") }
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = type,
            onValueChange = { type = it },
            label = { Text("Tipo de incidencia") }
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = obs,
            onValueChange = { obs = it },
            label = { Text("Observaciones") }
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            viewModel.insert(
                Incident(
                    machineId   = machineId,
                    date        = date,
                    type        = type,
                    observations= obs,
                    status      = "open"
                )
            )
            onSave()
        }) {
            Text("Guardar incidencia")
        }
    }
}
