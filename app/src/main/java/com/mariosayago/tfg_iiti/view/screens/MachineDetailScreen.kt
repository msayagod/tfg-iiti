package com.mariosayago.tfg_iiti.view.screens

import androidx.benchmark.perfetto.ExperimentalPerfettoTraceProcessorApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
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
import com.mariosayago.tfg_iiti.viewmodel.MachineViewModel

@OptIn(ExperimentalPerfettoTraceProcessorApi::class)
@Composable
fun MachineDetailScreen(
    machineId: Long,
    onEditClick: (Long) -> Unit, // Añadir parámetro para editar
    onDeleteClick: (Long) -> Unit, // Añadir parámetro para eliminar
    onEditSlotsClick: (Long) -> Unit, // Añadir parámetro para editar slots
    onNewIncidentClick: (Long) -> Unit, // Añadir parámetro para nueva incidencia
    viewModel: MachineViewModel = hiltViewModel()
) {
    // Recuperamos la lista de máquinas
    val list by viewModel
        .getMachineWithSlotAndProductList(machineId)
        .collectAsState(initial = null)
    //Cogemos la primera
    val data = list?.firstOrNull()
    data?.let { mwsp ->
        Column(Modifier.padding(16.dp)) {

            // Botones Editar / Eliminar
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = { onEditClick(machineId) }) {
                    Text("Editar")
                }
                Button(onClick = { onDeleteClick(machineId) }) {
                    Text("Eliminar")
                }

                Button(onClick = { onEditSlotsClick(machineId) }) { Text("Editar slots") }

                Button(onClick = { onNewIncidentClick(machineId) }) {
                    Text("Nueva incidencia")
                }

            }
            Spacer(Modifier.height(16.dp))

            // Datos máquina
            Text(mwsp.machine.name, style = MaterialTheme.typography.titleLarge)
            Text("Ubicación: ${mwsp.machine.location}")
            Spacer(Modifier.height(16.dp))

            //Slots con productos
            Text("Slots:", style = MaterialTheme.typography.titleMedium)
            mwsp.slots.forEach { slotWithProduct ->
                val slot = slotWithProduct.slot
                val prod = slotWithProduct.product
                Text(
                    "• Slot ${slot.row}-${slot.column}: " +
                            "${slot.currentStock} uds. de " +
                            (prod?.name ?: "—") +
                            (prod?.let { " (€${it.price})" } ?: "")
                )
            }
            // Incidencias…
        }
    }
}

