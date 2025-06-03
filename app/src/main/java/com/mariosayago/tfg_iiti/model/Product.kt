package com.mariosayago.tfg_iiti.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val price: Double,
    val imagePath: String? = null // opcional, por si se añade más adelante manualmente (si lo subo en csv por ejemplo)

)
