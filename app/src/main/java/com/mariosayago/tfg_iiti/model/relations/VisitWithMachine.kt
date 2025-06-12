package com.mariosayago.tfg_iiti.model.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.mariosayago.tfg_iiti.model.entities.Machine
import com.mariosayago.tfg_iiti.model.entities.Visit

data class VisitWithMachine(
    @Embedded val visit: Visit,
    @Relation(
        parentColumn = "machineId",
        entityColumn = "id"
    )
    val machine: Machine
)
