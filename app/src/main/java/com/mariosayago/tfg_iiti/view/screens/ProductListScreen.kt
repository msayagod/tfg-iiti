package com.mariosayago.tfg_iiti.view.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mariosayago.tfg_iiti.viewmodel.ProductViewModel

@Composable
fun ProductListScreen(
    onAddProduct: () -> Unit,
    onEditProduct: (Long) -> Unit,
    viewModel: ProductViewModel = hiltViewModel()
) {
    val products by viewModel.products.collectAsState()


    //Box con la lista de productos y un botón para añadir y otro para editar
    Box(Modifier.fillMaxSize()) {
        LazyColumn {
            items(products) { p ->
                Text(
                    text = "${p.name} — €${"%.2f".format(p.price)}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onEditProduct(p.id) } // Navegar a la pantalla de edición
                        .padding(16.dp)
                )
                HorizontalDivider()
            }
        }
        // Botón para añadir un nuevo producto
        ExtendedFloatingActionButton(
            text = { Text("Añadir") },
            icon = { Icon(Icons.Default.Add, contentDescription = "Añadir") },
            onClick = onAddProduct,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )

    }
}
