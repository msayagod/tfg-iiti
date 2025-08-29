package com.mariosayago.tfg_iiti.view.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mariosayago.tfg_iiti.viewmodel.IncidentViewModel

@Composable
fun IncidentDetailScreen(
    incidentId: Long,
    onBack: () -> Unit,
    viewModel: IncidentViewModel = hiltViewModel()
) {
    val incidentWithVisit by viewModel.getIncidentById(incidentId).collectAsState(initial = null)
    incidentWithVisit?.let {
        val incident = it.incident
        Column(Modifier.padding(16.dp)) {
            Text("Incidencia #${incident.id}", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text("Máquina: ${incident.machineId}")
            Text("Fecha: ${it.visit.date}") // ✅ aquí ya funciona
            Text("Observaciones: ${incident.observations ?: "—"}")
            Text("Estado: ${incident.status}")

            Spacer(Modifier.height(16.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = {
                    viewModel.closeIncident(incident.id)
                    onBack()
                }) {
                    Text("Cerrar")
                }
                Button(onClick = {
                    viewModel.delete(incident)
                    onBack()
                }) {
                    Text("Eliminar")
                }
                Button(onClick = onBack) {
                    Text("Volver")
                }
            }
        }
    }
}