package com.mariosayago.tfg_iiti.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "visits")
data class Visit( //MachineOperation
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val machineId: Long,
    val date: String, // "yyyy-mm-ss"
    val actualCash: Double //Importe recogido de la maquina
)
