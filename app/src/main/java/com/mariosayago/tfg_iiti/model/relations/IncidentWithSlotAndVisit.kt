package com.mariosayago.tfg_iiti.model.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.mariosayago.tfg_iiti.model.entities.Incident
import com.mariosayago.tfg_iiti.model.entities.Slot
import com.mariosayago.tfg_iiti.model.entities.Visit

data class IncidentWithSlotAndVisit(
    @Embedded val incident: Incident,

    @Relation(
        parentColumn = "slotId",
        entityColumn = "id",
        entity = Slot::class
    )
    val slotWithProduct: SlotWithProduct?, // puede ser null si no hay slotId

    @Relation(
        parentColumn = "visitId",
        entityColumn = "id"
    )
    val visit: Visit
)

