package com.mariosayago.tfg_iiti.view.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.mariosayago.tfg_iiti.viewmodel.SlotViewModel
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun SlotListScreen(
    machineId: Long,
    onSlotClick: (Long) -> Unit,
    slotVm: SlotViewModel = hiltViewModel()
) {

    // Memoriza la llamada al Flow para que no se vuelva a crear en cada recomposición
    // De esta manera no se queda intermitente la pantalla mostrando el mensaje y los slots
    val slotsFlow = remember(machineId) { slotVm.slotsWithProduct(machineId) }

    // Colecta el stateflow una vez para obtener la lista de slots
    val slots by slotsFlow.collectAsState(initial = emptyList()) // usa initial emptyList() para evitar null

    // Si no hay slots, muestra un mensaje
    if (slots.isEmpty()) {
        Text("Esta máquina aún no tiene slots", modifier = Modifier.padding(16.dp))
    } else {
        LazyColumn {
            items(slots) { sp ->
                Text(
                    "Slot ${sp.slot.rowIndex}-${sp.slot.colIndex}: " +
                            "${sp.slot.currentStock} uds. de ${sp.product?.name ?: "—"}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSlotClick(sp.slot.id) }
                        .padding(16.dp)
                )
                HorizontalDivider()
            }
        }
    }
}
