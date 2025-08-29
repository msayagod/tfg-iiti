package com.mariosayago.tfg_iiti.view.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mariosayago.tfg_iiti.viewmodel.IncidentViewModel
import com.mariosayago.tfg_iiti.viewmodel.MachineViewModel

@Composable
fun IncidentListScreen(
    onIncidentClick: (Long) -> Unit,
    onViewClosed: () -> Unit,
    incidentVm: IncidentViewModel = hiltViewModel(),
    machineVm: MachineViewModel = hiltViewModel()
) {
    // 1. Flows
    val incidents by incidentVm.openIncidents.collectAsState()
    val machines by machineVm.machines.collectAsState()

    Column {
        // Enlace a "ver cerradas"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "Ver incidencias cerradas",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable(onClick = onViewClosed)
                    .padding(8.dp)
            )
        }

        LazyColumn(Modifier.fillMaxSize()) {
            if (incidents.isEmpty()) {
                item {
                    Box(
                        Modifier.fillMaxSize().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No hay incidencias abiertas.")
                    }
                }
            } else {
                items(incidents) { inc ->
                    // 2. Buscamos el nombre de la máquina en la lista
                    val machineName =
                        machines.find { it.id == inc.visit.machineId }?.name
                            ?: "Máquina desconocida"

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onIncidentClick(inc.incident.id) }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = machineName,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Incidencia — ${inc.visit.date}",
                                style = MaterialTheme.typography.bodySmall
                            )

                        }
                        Text(
                            text = "Ver",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}

