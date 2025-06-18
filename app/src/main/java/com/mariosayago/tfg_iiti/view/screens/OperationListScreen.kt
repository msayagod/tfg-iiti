package com.mariosayago.tfg_iiti.view.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.mariosayago.tfg_iiti.viewmodel.SlotViewModel
import com.mariosayago.tfg_iiti.model.relations.SlotWithProduct

@Composable
fun OperationListScreen(
    machineId: Long,
    onSlotClick: (Long) -> Unit,
    slotVm: SlotViewModel = hiltViewModel(),

) {
    // 1) Cargamos los slots junto con su producto
    val slotsWithProduct by slotVm
        .getSlotsWithProductByMachine(machineId)
        .collectAsState(initial = emptyList())

    // 2) Si aún no hay datos, mostramos un loader
    if (slotsWithProduct.isEmpty()) {
        Box(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    // 3) Listado de slots
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(slotsWithProduct) { sp: SlotWithProduct ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSlotClick(sp.slot.id) }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Imagen pequeña si existe
                sp.product?.imagePath?.let { url ->
                    AsyncImage(
                        model = url,
                        contentDescription = sp.product.name,
                        modifier = Modifier
                            .size(40.dp)
                            .padding(end = 8.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Slot ${sp.slot.rowIndex}-${sp.slot.colIndex}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = sp.product?.name ?: "—",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Text(
                    text = "${sp.slot.currentStock} uds.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}
