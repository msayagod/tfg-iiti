package com.mariosayago.tfg_iiti.view.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.benchmark.perfetto.ExperimentalPerfettoTraceProcessorApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mariosayago.tfg_iiti.model.entities.Operation
import com.mariosayago.tfg_iiti.viewmodel.OperationViewModel
import com.mariosayago.tfg_iiti.viewmodel.SlotViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalPerfettoTraceProcessorApi::class)
@Composable
fun OperationFormScreen(
    slotId: Long,
    onDone: () -> Unit,
    opVm: OperationViewModel = hiltViewModel(),
    slotVm: SlotViewModel = hiltViewModel()
) {
    // 1) Cargo slot + producto
    val slotWithProd by slotVm
        .getSlotWithProduct(slotId)
        .collectAsState(initial = null)
    if (slotWithProd == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Cargando…")
        }
        return
    }
    val (slot, prod) = slotWithProd!!


    // Leer operacion existente de hoy si ya hay
    // 1) Cadena de hoy en ISO
    var dateToday = LocalDate.now()
    val todayStr = dateToday.toString()
    // 2) Busca si ya existe una operación hoy para este slot
    val existingOps by opVm
        .todayOpsForSlot(slot.id, todayStr)
        .collectAsState(initial = emptyList())
    val existingOp = existingOps.firstOrNull()


    // 3) Estados locales
    //CurrentStock si el slot no está operado, sino el stock de la operación
    var observedStock by rememberSaveable(existingOp?.id ?: 0L) {
        mutableIntStateOf(existingOp?.observedStock ?: slot.currentStock)
    }
    var modeAuto by rememberSaveable { mutableStateOf(true) }
    //ReplenishedUnits si el slot esta operado, sino 0 (por defecto)
    var manualUnits by rememberSaveable(existingOp?.id ?: 0L) {
        mutableIntStateOf(existingOp?.replenishedUnits ?: 0)
    }


    val maxReplenish = slot.maxCapacity - observedStock
    val unitsToReplenish = if (modeAuto) maxReplenish else manualUnits
    val price = prod?.price ?: 0.0
    val estimatedRevenue = price * unitsToReplenish


    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Slot ${slot.rowIndex}-${slot.colIndex}", style = MaterialTheme.typography.titleLarge)
        Text("Producto: ${prod?.name ?: "—"}")
        Text("Capacidad máxima: ${slot.maxCapacity}")

        // ── STOCK OBSERVADO ─────────────────────────────────────
        Text("Stock observado:", style = MaterialTheme.typography.bodyMedium)
        if (existingOp == null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { observedStock = (observedStock - 1).coerceAtLeast(0) }) {
                    Icon(Icons.Default.Remove, contentDescription = "Menos")
                }
                Text(
                    text = "$observedStock",
                    modifier = Modifier.width(48.dp),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(Modifier.width(16.dp))
                OutlinedButton(onClick = { observedStock = 0 }) {
                    Text("Vaciar")
                }
            }
        } else {
            // operación existente: mostramos el stock fijo
            Text(
                text = "$observedStock",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(8.dp)
            )
        }

        // ── MODO REPOSICIÓN ────────────────────────────────────
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = modeAuto, onClick = { modeAuto = true })
            Text("Rellenar automáticamente")
            Spacer(Modifier.width(16.dp))
            RadioButton(selected = !modeAuto, onClick = { modeAuto = false })
            Text("Manual")
        }

        if (!modeAuto) {
            Text("Unidades a reponer:", style = MaterialTheme.typography.bodyMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    manualUnits = (manualUnits - 1).coerceAtLeast(0)
                }) {
                    Icon(Icons.Default.Remove, contentDescription = "Menos")
                }
                Text(
                    text = "$manualUnits",
                    modifier = Modifier.width(48.dp),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                IconButton(onClick = {
                    manualUnits = (manualUnits + 1).coerceAtMost(maxReplenish)
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Más")
                }
                Spacer(Modifier.width(16.dp))
                OutlinedButton(onClick = { manualUnits = maxReplenish }) {
                    Text("Rellenar")
                }
            }
        }

        // ── VALIDACIONES ─────────────────────────────────────────
        if (observedStock < 0 || observedStock > slot.maxCapacity) {
            Text("Stock observado fuera de rango!", color = MaterialTheme.colorScheme.error)
        }
        if (unitsToReplenish < 0 || unitsToReplenish > maxReplenish) {
            Text("Reposición supera límite!", color = MaterialTheme.colorScheme.error)
        }


        // ── GUARDAR ────────────────────────────────────────────
        val scope = rememberCoroutineScope()
        Button(
            onClick = {
                scope.launch {

                    // 2) Calcula el nuevo stock y lo actualiza
                    val newStock = observedStock + unitsToReplenish
                    slotVm.update(slot.copy(currentStock = newStock))


                    // 3) Crea la Operation con id = existingOp?.id ?: 0L
                    val op = Operation(
                        id = existingOp?.id ?: 0L, // id de la op si ya existe para el dia de hoy
                        slotId = slot.id,
                        date = todayStr,
                        observedStock = observedStock,
                        replenishedUnits = unitsToReplenish,
                        estimatedRevenue = estimatedRevenue
                    )

                    // 5) Inserta o reemplaza la operación con opVm (no slotVm)
                    opVm.insert(op)

                    // 6) Vuelve atrás en la UI
                    onDone()
                }
            },
            enabled = observedStock in 0..slot.maxCapacity
                    && unitsToReplenish in 0..maxReplenish
        ) {
            Text(if (existingOp == null) "Guardar" else "Actualizar")
        }
    }
}
