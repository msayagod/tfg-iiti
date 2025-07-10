package com.mariosayago.tfg_iiti.view.screens

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mariosayago.tfg_iiti.model.entities.Product
import com.mariosayago.tfg_iiti.viewmodel.ProductViewModel
import coil.compose.AsyncImage
import androidx.compose.ui.platform.LocalContext
import com.mariosayago.tfg_iiti.view.components.ImageUtils
import kotlinx.coroutines.launch
import java.io.IOException


@Composable
fun ProductFormScreen(
    productId: Long? = null,
    onSave: () -> Unit,
    viewModel: ProductViewModel = hiltViewModel()
) {

    // Context y scope
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Estado de los campos
    var name by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var imagePath by remember { mutableStateOf<String?>(null) }
    val isEditing = productId != null

    // picker para elegir imagen
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { pickedUri ->
            // arrancamos la copia en background
            scope.launch {
                try {
                    // esta función suspend copía la Uri a filesDir y devuelve la nueva ruta
                    val localPath = ImageUtils.copyUriToInternalStorage(context, pickedUri)
                    imagePath = localPath
                } catch (e: IOException) {
                    Log.e("ProductForm", "Error copiando imagen", e)
                }
            }
        }
    }

    // Si editamos, cargamos inicial
    LaunchedEffect(productId) {
        if (isEditing) {
            viewModel.products.collect { list ->
                list.find { it.id == productId }?.let {
                    name = it.name
                    priceText = it.price.toString()
                    imagePath = it.imagePath
                }
            }
        }
    }
    // Formulario
    Column(Modifier.padding(16.dp)) {

        // Botón y preview de imagen
        Button(onClick = { imagePicker.launch("image/*") }) {
            Text("Seleccionar imagen")
        }
        imagePath?.let { path ->
            AsyncImage(
                model = path,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(vertical = 8.dp)
            )
        }

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
            onValueChange = { priceText = it.filter { c -> c.isDigit() || c == '.' } },
            label = { Text("Precio") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        // Botón para guardar
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                val price = priceText.toDoubleOrNull() ?: 0.0
                val p = Product(
                    id = productId ?: 0L,
                    name = name,
                    price = price,
                    imagePath = imagePath
                )
                if (isEditing) {
                    viewModel.update(p)
                } else {
                    viewModel.insert(p)
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
