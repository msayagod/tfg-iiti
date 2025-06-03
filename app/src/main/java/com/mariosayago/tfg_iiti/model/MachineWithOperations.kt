package com.mariosayago.tfg_iiti.model

import androidx.room.Embedded
import androidx.room.Relation

data class MachineWithOperations(
    @Embedded val machine: Machine,
    @Relation(
        parentColumn = "id",
        entityColumn = "machineId"
    )
    val operations: List<Operation>
)
