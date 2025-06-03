package com.mariosayago.tfg_iiti.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "incidents")
data class Incident(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val machineId: Long,
    val date: String,
    val type: String, // Por ejemplo: "Error en pago", "Stock incorrecto", etc.
    val observations:String?
)
