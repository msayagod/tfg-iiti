package com.mariosayago.tfg_iiti.model

import java.time.LocalDate

data class CalendarVisit(
    val machineId: Long,
    val machineName: String,
    val date: LocalDate,
    val isRecurring: Boolean
)
