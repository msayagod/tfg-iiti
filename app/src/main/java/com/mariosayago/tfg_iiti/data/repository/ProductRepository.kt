package com.mariosayago.tfg_iiti.data.repository

import com.mariosayago.tfg_iiti.data.dao.ProductDao
import com.mariosayago.tfg_iiti.model.entities.Product
import kotlinx.coroutines.flow.Flow

class ProductRepository(private val productDao: ProductDao) {

    fun getAllProducts(): Flow<List<Product>> = productDao.getAllProducts()

    suspend fun insertProduct(product: Product): Long =
        productDao.insertProduct(product)

    suspend fun updateProduct(product: Product) =
        productDao.updateProduct(product)

    suspend fun deleteProduct(product: Product) =
        productDao.deleteProduct(product)
}
