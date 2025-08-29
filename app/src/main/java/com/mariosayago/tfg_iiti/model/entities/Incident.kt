package com.mariosayago.tfg_iiti.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "incidents")
data class Incident(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val visitId: Long,
    val machineId: Long,
    val slotId: Long?,
    val observations:String?,
    val status: String // "open", "closed"
)
