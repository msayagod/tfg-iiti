package com.mariosayago.tfg_iiti.model.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.mariosayago.tfg_iiti.model.entities.Incident
import com.mariosayago.tfg_iiti.model.entities.Machine

data class MachineWithIncidents(
    @Embedded val machine: Machine,
    @Relation(
        parentColumn = "id",
        entityColumn = "machineId"
    )
    val incidents: List<Incident>
)
