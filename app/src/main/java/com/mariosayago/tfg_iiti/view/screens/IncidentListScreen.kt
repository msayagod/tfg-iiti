package com.mariosayago.tfg_iiti.view.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mariosayago.tfg_iiti.viewmodel.MachineViewModel

@Composable
fun IncidentListScreen(
    onIncidentClick: (Long) -> Unit = {},  //valor por defecto
    viewModel: MachineViewModel = hiltViewModel()
) {
    // Sólo máquinas con al menos 1 incidencia
    val machinesWithInc by viewModel.machinesWithIncidents().collectAsState()
    LazyColumn {
        items(machinesWithInc) { mwi ->
            // por cada máquina con sus incidencias, lanzamos un ítem por incidencia
            mwi.incidents.forEach { incident ->
                Text(
                    text = "${mwi.machine.name}: ${incident.type}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onIncidentClick(incident.id) }  // aquí uso incident.id
                        .padding(16.dp)
                )
                HorizontalDivider()
            }
        }
    }
}
