package com.mariosayago.tfg_iiti.view.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mariosayago.tfg_iiti.model.entities.Product
import com.mariosayago.tfg_iiti.viewmodel.ProductViewModel

@Composable
fun ProductFormScreen(
    productId: Long? = null,
    onSave: () -> Unit,
    viewModel: ProductViewModel = hiltViewModel()
) {
    // Estado de los campos
    var name by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    val isEditing = productId != null

    // Si editamos, cargamos inicial
    LaunchedEffect(productId) {
        if (isEditing) {
            viewModel.products.collect { list ->
                list.find { it.id == productId }?.let {
                    name = it.name
                    priceText = it.price.toString()
                }
            }
        }
    }
    // Formulario
    Column(Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        // Campo para el precio
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = priceText,
            onValueChange = { priceText = it.filter { c-> c.isDigit()||c=='.' } },
            label = { Text("Precio") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        // Botón para guardar
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                val price = priceText.toDoubleOrNull() ?: 0.0
                if (isEditing) {
                    viewModel.update(Product(id = productId, name = name, price = price))
                } else {
                    viewModel.insert(Product(name = name, price = price, imagePath = null))
                }
                onSave()
            },
            enabled = name.isNotBlank() && priceText.toDoubleOrNull() != null,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Guardar")
        }
    }
}
