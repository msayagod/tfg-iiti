package com.mariosayago.tfg_iiti.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "slots")
data class Slot(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val machineId: Long,
    val productId: Long?,
    val row: Int,
    val column: Int,
    val maxCapacity: Int,
    val currentStock: Int,
    val combinedWithNext: Boolean = false // para indicar si está combinado con el hueco adyacente

)
