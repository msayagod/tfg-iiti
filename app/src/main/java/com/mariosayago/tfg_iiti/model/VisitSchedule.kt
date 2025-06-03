package com.mariosayago.tfg_iiti.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "visit_schedules")
data class VisitSchedule(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val machineId: Long,
    val frequency: String, // Ej: "w", "m" (weekly, monthly)
    val daysOfWeek: String, // "1,3,5" → Lunes, Miércoles, Viernes
    val hour: String? // Puede ser nulo si es libre
)
