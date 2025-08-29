package com.mariosayago.tfg_iiti.view.screens

import android.widget.Toast
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
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*



@Composable
fun ProductListScreen(
    onAddProduct: () -> Unit,
    onEditProduct: (Long) -> Unit,
    viewModel: ProductViewModel = hiltViewModel()
) {
    val products by viewModel.products.collectAsState()
    val context = LocalContext.current // Para acceder al contexto desde el ViewModel


    // Launcher para elegir fichero
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult

        // 1) Filtrar por extensión
        val path = uri.lastPathSegment?.lowercase() ?: ""
        if (!path.endsWith(".csv") && !path.endsWith(".txt")) {
            Toast.makeText(
                context,
                "Formato inválido: sólo .csv o .txt",
                Toast.LENGTH_LONG
            ).show()
            return@rememberLauncherForActivityResult
        }

        // 2) Intentar parsear / importar
        val success = viewModel.importFromUri(context, uri)
        if (!success) {
            Toast.makeText(
                context,
                "Fichero inválido: cada línea debe ser “nombre,precio”",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // Control para mostrar/ocultar la mini-instrucción
    var showInfo by remember { mutableStateOf(false) }


    Box(Modifier.fillMaxSize()) {

        //Mensaje si no hay productos
        if (products.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay productos disponibles.")
            }

            // Lista de productos
        } else {
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
        }

        // Tamaño de los botones flotante
        val fabSize = Modifier.size(width = 140.dp, height = 56.dp)

        // Botones flotantes
        Column(
            Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.End

        ) {

            // Botón flotante para añadir un nuevo producto
            ExtendedFloatingActionButton(
                modifier = fabSize,
                text = { Text("Añadir") },
                icon = { Icon(Icons.Default.Add, contentDescription = "Añadir") },
                onClick = onAddProduct
            )

            // Botón flotante para importar un CSV con el info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {

                IconButton(onClick = { showInfo = true }) {
                    Icon(Icons.Default.Info, contentDescription = "Cómo importar")
                }

                ExtendedFloatingActionButton(
                    modifier = fabSize,
                    icon = { Icon(Icons.Default.FileUpload, contentDescription = "Importar") },
                    text = { Text("Importar") },
                    onClick = { importLauncher.launch("text/csv") }
                )

            }
        }

        // Diálogo de información
        if (showInfo) {
            AlertDialog(
                onDismissRequest = { showInfo = false },
                confirmButton = {
                    TextButton(onClick = { showInfo = false }) {
                        Text("Entendido")
                    }
                },
                title = { Text("¿Cómo importar?") },
                text = {
                    Text(
                        """
                        Selecciona un archivo CSV o TXT con el siguiente formato por línea:
                        nombre,precio
                        
                        • nombre: texto (sin comillas)
                        • precio: número decimal, por ejemplo 1.50
                        
                        Si el archivo no cumple este formato, la importación fallará y se mostrará un error.
                        """.trimIndent()
                    )
                }
            )
        }
    }
}
