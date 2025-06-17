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

        OutlinedTextField(
            value = stockText,
            onValueChange = { stockText = it.filter(Char::isDigit) },
            label = { Text("Stock actual") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botón Guardar cambios de producto/stock
        Button(
            onClick = {
                slotVm.update(
                    slotWithProduct.slot.copy(
                        productId = selectedPid,
                        currentStock = stockText.toIntOrNull()
                            ?: slotWithProduct.slot.currentStock
                    )
                )
                onSave()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar")
        }

        Spacer(Modifier.height(16.dp))

    }
}

