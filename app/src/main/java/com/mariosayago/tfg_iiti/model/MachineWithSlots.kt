package com.mariosayago.tfg_iiti.model

import androidx.room.Embedded
import androidx.room.Relation

data class MachineWithSlots(
    @Embedded val machine: Machine,
    @Relation(
        parentColumn = "id",
        entityColumn = "machineId"
    )
    val slots:List<Slot>
)