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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SlotListScreen(
    machineId: Long,
    onSlotClick: (Long) -> Unit,
    slotVm: SlotViewModel = hiltViewModel()
) {
    val slots by slotVm.slotsWithProduct(machineId).collectAsState()
    LazyColumn {
        items(slots) { sp ->
            Text(
                "Slot ${sp.slot.row}-${sp.slot.column}: " +
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
