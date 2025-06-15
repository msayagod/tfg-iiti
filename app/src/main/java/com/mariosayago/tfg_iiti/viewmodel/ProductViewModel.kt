package com.mariosayago.tfg_iiti.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mariosayago.tfg_iiti.model.entities.Product
import com.mariosayago.tfg_iiti.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.content.Context
import android.net.Uri
import android.widget.Toast


@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    val products: StateFlow<List<Product>> =
        repository.getAllProducts()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(product: Product) {
        viewModelScope.launch { repository.insertProduct(product) }
    }

    fun update(product: Product) {
        viewModelScope.launch { repository.updateProduct(product) }
    }

    fun delete(product: Product) {
        viewModelScope.launch { repository.deleteProduct(product) }
    }

    // Importa productos desde un CSV con formato “name,price” en cada línea
    fun importFromUri(context: Context, uri: Uri): Boolean {
        var success = false
        try {
            // Abrimos el stream y leemos línea a línea
            context.contentResolver.openInputStream(uri)
                ?.bufferedReader()
                .use { reader ->
                    var count = 0
                    reader
                        ?.lineSequence()
                        ?.mapNotNull { line ->
                            val parts = line.split(",")
                            if (parts.size >= 2) {
                                val name = parts[0].trim().takeIf { it.isNotEmpty() }
                                    ?: return@mapNotNull null
                                val price = parts[1].trim().toDoubleOrNull()
                                    ?: return@mapNotNull null
                                Product(name = name, price = price)
                            } else null
                        }
                        ?.forEach { product ->
                            // lanzamos la inserción en paralelo
                            viewModelScope.launch { //Así no se bloquea la UI (el hilo)
                                repository.insertProduct(product)
                            }
                            count++
                        }
                    Toast.makeText(context, "Importados $count productos", Toast.LENGTH_SHORT)
                        .show()
                    success = count > 0
                }
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Error importando (asegúrate de usar “nombre,precio”): ${e.message}",
                Toast.LENGTH_LONG
            ).show()
            success = false
        }
        return success
    }
}

