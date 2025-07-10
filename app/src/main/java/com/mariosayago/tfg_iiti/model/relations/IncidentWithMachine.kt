package com.mariosayago.tfg_iiti.model.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.mariosayago.tfg_iiti.model.entities.Incident
import com.mariosayago.tfg_iiti.model.entities.Machine

data class IncidentWithMachine(
    @Embedded val incident: Incident,
    @Relation(
        parentColumn = "machineId",
        entityColumn = "id",
        entity = Machine::class
    )
    val machine: Machine
)
