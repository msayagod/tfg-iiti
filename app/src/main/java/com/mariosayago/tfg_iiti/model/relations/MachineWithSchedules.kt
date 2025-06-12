package com.mariosayago.tfg_iiti.model.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.mariosayago.tfg_iiti.model.entities.Machine
import com.mariosayago.tfg_iiti.model.entities.VisitSchedule

data class MachineWithSchedules(
    @Embedded val machine: Machine,
    @Relation(
        parentColumn = "id",
        entityColumn = "machineId"
    )
    val schedules: List<VisitSchedule>
)