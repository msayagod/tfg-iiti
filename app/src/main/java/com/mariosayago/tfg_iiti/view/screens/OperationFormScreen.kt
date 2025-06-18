package com.mariosayago.tfg_iiti.view.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mariosayago.tfg_iiti.model.entities.Operation
import com.mariosayago.tfg_iiti.viewmodel.OperationViewModel
import com.mariosayago.tfg_iiti.viewmodel.SlotViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.absoluteValue

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OperationFormScreen(
    slotId: Long,
    onDone: () -> Unit,
    onRegisterIncident: (Long) -> Unit,    // callback para ir a registrar incidencia
    opVm: OperationViewModel = hiltViewModel(),
    slotVm: SlotViewModel = hiltViewModel()
) {
    // 1) Cargo el slot con producto
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


    // 2) Estados locales
    var observedStock by rememberSaveable { mutableStateOf(slot?.currentStock.toString()) }
    var modeAuto by rememberSaveable { mutableStateOf(true) }
    var manualUnits by rememberSaveable { mutableStateOf("") }
    var actualCash by rememberSaveable { mutableStateOf("") }

    val maxReplenish = slot.maxCapacity - observedStock.toIntOrNull().orZero()
    val unitsToReplenish = if (modeAuto) maxReplenish else manualUnits.toIntOrNull().orZero()
    val estimatedRevenue = prod?.price.orZero() * unitsToReplenish
    val diff = actualCash.toDoubleOrNull().orZero() - estimatedRevenue

    Column(Modifier
        .fillMaxSize()
        .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Slot ${slot.rowIndex}-${slot.colIndex}", style = MaterialTheme.typography.titleLarge)
        prod?.let { Text("Producto: ${it.name}") }
        Text("Capacidad máxima: ${slot.maxCapacity}")
        OutlinedTextField(
            value = observedStock,
            onValueChange = { if (it.all(Char::isDigit)) observedStock = it },
            label = { Text("Stock observado") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        // Selector modo
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = modeAuto, onCheckedChange = { modeAuto = true })
            Text("Rellenar automáticamente")
            Spacer(Modifier.width(16.dp))
            Checkbox(checked = !modeAuto, onCheckedChange = { modeAuto = false })
            Text("Introducir unidades manualmente")
        }

        if (!modeAuto) {
            OutlinedTextField(
                value = manualUnits,
                onValueChange = { input ->
                    if (input.all { it.isDigit() }) {
                        manualUnits = input
                    }
                },
                label = { Text("Unidades a reponer") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Validación
        if (unitsToReplenish > maxReplenish) {
            Text("¡No puedes reponer más de $maxReplenish unidades!",
                color = MaterialTheme.colorScheme.error)
        }

        // Importe estimado
        Text("Importe estimado: %.2f €".format(estimatedRevenue))

        OutlinedTextField(
            value = actualCash,
            onValueChange = { v ->
                if (v.isEmpty() || v.toDoubleOrNull()!=null) actualCash = v
            },
            label = { Text("Importe real recogido") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        // Discrepancia
        if (actualCash.isNotBlank() && diff.absoluteValue > 0.01) {
            Text("Discrepancia: %.2f €".format(diff),
                color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(24.dp))

        // Botones
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { onRegisterIncident(slot.id) }) {
                Text("Registrar incidencia")
            }
            Spacer(Modifier.weight(1f))
            Button(
                onClick = {
                    val op = Operation(
                        slotId            = slot.id,
                        date              = LocalDateTime.now()
                            .format(DateTimeFormatter.ISO_DATE_TIME),
                        observedStock     = observedStock.toIntOrNull().orZero(),
                        replenishedUnits  = unitsToReplenish,
                        estimatedRevenue  = estimatedRevenue,
                        actualCash        = actualCash.toDoubleOrNull()
                    )
                    opVm.insert(op)
                    onDone()
                },
                enabled = (unitsToReplenish in 0..maxReplenish)
            ) {
                Text("Guardar")
            }
        }
    }
}

// Para no liarnos con nullables / toIntOrNull
private fun Int?.orZero() = this ?: 0
private fun Double?.orZero() = this ?: 0.0