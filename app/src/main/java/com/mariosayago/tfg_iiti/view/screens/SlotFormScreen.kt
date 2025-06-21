package com.mariosayago.tfg_iiti.view.screens


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.mariosayago.tfg_iiti.viewmodel.ProductViewModel
import com.mariosayago.tfg_iiti.viewmodel.SlotViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.mutableLongStateOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SlotFormScreen(
    machineId: Long,
    slotId: Long,
    onSave: () -> Unit,
    onAddProduct: () -> Unit,
    slotVm: SlotViewModel = hiltViewModel(),
    productVm: ProductViewModel = hiltViewModel()
) {
    // 1) carga el SlotWithProduct (nullable)
    val sp by slotVm
        .getSlotWithProduct(slotId)
        .collectAsState(initial = null)

    // --- EARLY RETURN: si aún no ha llegado el dato, salimos ---
    val slotWithProduct = sp ?: return

    // 2) lista de productos desde la BBDD
    val products by productVm.products.collectAsState()

    // Si no hay productos, aviso y botón para ir a crearlos
    if (products.isEmpty()) {
        Column(Modifier.padding(16.dp)) {
            Text("No hay productos. Primero añade alguno.")
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onAddProduct) {
                Text("Ir a Productos")
            }
        }
        return
    }


    // 3) Estados locales para el dropdown y stock
    var expanded by remember { mutableStateOf(false) }
    var selectedPid by remember {
        mutableLongStateOf(slotWithProduct.slot.productId ?: products.first().id)
    }
    var stockText by remember {
        mutableStateOf(slotWithProduct.slot.currentStock.toString())
    }
    var capacityText by remember {
        mutableStateOf(slotWithProduct.slot.maxCapacity.toString())
    }

    // --- Validaciones ---
    // Parseos a Int y rangos
    val capacityVal = capacityText.toIntOrNull() ?: 0
    val stockVal = stockText.toIntOrNull() ?: 0

    // Comprueba que capacity está entre 1 y 100
    val isCapacityValid = capacityVal in 1..100
    // Comprueba que stock no supere capacity y sea ≥ 0
    val isStockValid = stockVal in 0..capacityVal

    val isFormValid = isCapacityValid && isStockValid

    // 4) Nombre visible
    val selectedName = products.find { it.id == selectedPid }?.name ?: "—"

    Column(Modifier.padding(16.dp)) {
        // Dropdown “manual” con TextField + DropdownMenu
        Box {
            OutlinedTextField(
                value = selectedName,
                onValueChange = { /* readOnly */ },
                label = { Text("Producto") },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        Modifier.clickable { expanded = true }
                    )
                }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                products.forEach { prod ->
                    DropdownMenuItem(
                        text = { Text(prod.name) },
                        onClick = {
                            selectedPid = prod.id
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        // Capacidad máxima
        OutlinedTextField(
            value = capacityText,
            //Recordatorio. it es como si el lambda por defecto es it-> (omitido)
            onValueChange = { capacityText = it.filter(Char::isDigit) },
            label = { Text("Capacidad máxima") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            isError = !isCapacityValid && capacityText.isNotBlank()
        )

        if (!isCapacityValid && capacityText.isNotBlank()) {
            Text(
                "Debe ser un número entre 1 y 100",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(Modifier.height(24.dp))

        //Stock actual
        OutlinedTextField(
            value = stockText,
            onValueChange = { stockText = it.filter(Char::isDigit) },
            label = { Text("Stock actual") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            isError = !isStockValid && stockText.isNotBlank()
        )

        if (!isStockValid && stockText.isNotBlank()) {
            Text(
                "Debe ser un número entre 0 y $capacityVal",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))


        // Botón Guardar
        Button(
            onClick = {
                slotVm.update(
                    slotWithProduct.slot.copy(
                        productId = selectedPid,
                        currentStock = stockVal,
                        maxCapacity = capacityVal
                    )
                )
                onSave()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isFormValid
        ) {
            Text("Guardar")
        }


    }
}

