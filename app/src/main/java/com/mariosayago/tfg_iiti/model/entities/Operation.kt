package com.mariosayago.tfg_iiti.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "operations")
data class Operation(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val slotId: Long,
    val date: String, // formato ISO
    val observedStock: Int,
    val replenishedUnits: Int,
    val estimatedRevenue: Double,
    val actualCash: Double?
)
