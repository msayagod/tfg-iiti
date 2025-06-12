package com.mariosayago.tfg_iiti.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "visits")
data class Visit(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val machineId: Long,
    val date: String, // "yyyy-mm-ss"
    val scheduleId: Long? = null // Si proviene de una planificación
)
