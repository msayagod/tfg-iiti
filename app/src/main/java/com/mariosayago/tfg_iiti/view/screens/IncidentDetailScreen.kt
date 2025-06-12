package com.mariosayago.tfg_iiti.view.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
    viewModel: IncidentViewModel = hiltViewModel()
) {
    val incident by viewModel.getIncidentById(incidentId).collectAsState(initial = null)
    incident?.let {
        Column(Modifier.padding(16.dp)) {
            Text("Incidencia #${it.id}", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text("Máquina: ${it.machineId}")
            Text("Fecha: ${it.date}")
            Text("Tipo: ${it.type}")
            Text("Observaciones: ${it.observations ?: "—"}")
        }
    }
}
