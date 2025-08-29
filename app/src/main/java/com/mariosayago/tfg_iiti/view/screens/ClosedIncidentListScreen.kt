package com.mariosayago.tfg_iiti.view.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mariosayago.tfg_iiti.model.entities.Incident
import com.mariosayago.tfg_iiti.viewmodel.IncidentViewModel
import com.mariosayago.tfg_iiti.viewmodel.MachineViewModel
import androidx.compose.runtime.getValue
import com.mariosayago.tfg_iiti.model.relations.IncidentWithSlotAndVisit


@Composable
fun ClosedIncidentListScreen(
    onIncidentClick: (Long) -> Unit,
    viewModel: IncidentViewModel = hiltViewModel()
) {
    val incidents by viewModel.closedIncidents.collectAsState()
    LazyColumn(Modifier.fillMaxSize()) {
        items(incidents) { inc ->
            // muestra igual que en IncidentListScreen
            IncidentRow(inc, onIncidentClick)
            HorizontalDivider()
        }
    }
}

@Composable
fun IncidentRow(
    incidentWithSlot: IncidentWithSlotAndVisit,
    onIncidentClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    machineViewModel: MachineViewModel = hiltViewModel()
) {
    val incident = incidentWithSlot.incident
    val visitDate = incidentWithSlot.visit.date
    val machines by machineViewModel.machines.collectAsState()
    val machineName = machines.find { it.id == incident.machineId }?.name ?: "?"

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onIncidentClick(incident.id) }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = machineName, style = MaterialTheme.typography.titleMedium)
            Text(
                text = "${incident.observations ?: "Sin observaciones"} — $visitDate",
                style = MaterialTheme.typography.bodySmall
            )
        }
        Text(text = "Ver", color = MaterialTheme.colorScheme.primary)
    }
}

