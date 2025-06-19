package com.mariosayago.tfg_iiti.view.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.mariosayago.tfg_iiti.model.relations.SlotWithProduct
import com.mariosayago.tfg_iiti.model.relations.OperationWithSlot
import com.mariosayago.tfg_iiti.viewmodel.OperationViewModel
import com.mariosayago.tfg_iiti.viewmodel.SlotViewModel
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OperationListScreen(
    machineId: Long,
    onSlotClick: (Long) -> Unit,
    slotVm: SlotViewModel = hiltViewModel(),
    opVm: OperationViewModel = hiltViewModel()
) {

    // 1) Slots + productos
    val slotsWithProduct by slotVm
        .getSlotsWithProductByMachine(machineId)
        .collectAsState(initial = emptyList())

    // 2) Todas las operaciones de hoy
    val today = LocalDate.now().toString()
    val todayOpsAll: List<OperationWithSlot> by opVm
        .getOperationsWithSlotByDate(today)
        .collectAsState(initial = emptyList())

    // 3) Filtramos sólo las de esta máquina
    val todayOps: List<OperationWithSlot> = remember(todayOpsAll) {
        todayOpsAll.filter { it.slotWithProduct.slot.machineId == machineId }
    }

    // 4) IDs de slots ya operados
    val doneIds: Set<Long> = remember(todayOps) {
        todayOps.map { it.slotWithProduct.slot.id }.toSet()
    }

    // 5) Toggle mostrar operados
    var showOperated by rememberSaveable { mutableStateOf(false) }

    // 6) Loader si no hay slots
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

    Column(Modifier.fillMaxSize()) {
        // Checkbox “Mostrar slots operados”
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = showOperated,
                onCheckedChange = { showOperated = it }
            )
            Spacer(Modifier.width(8.dp))
            Text("Mostrar slots operados")
        }

        // 7) Listado filtrado
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = slotsWithProduct.filter { showOperated || it.slot.id !in doneIds }
            ) { sp: SlotWithProduct ->
                val isDone = sp.slot.id in doneIds
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (isDone) Color.LightGray else Color.Transparent)
                        .clickable(enabled = !isDone) { onSlotClick(sp.slot.id) }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
}


