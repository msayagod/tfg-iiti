package com.mariosayago.tfg_iiti.model

import androidx.room.Embedded
import androidx.room.Relation

data class VisitWithMachine(
    @Embedded val visit: Visit,
    @Relation(
        parentColumn = "machineId",
        entityColumn = "id"
    )
    val machine: Machine
)
