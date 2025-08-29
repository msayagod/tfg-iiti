package com.mariosayago.tfg_iiti.model.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.mariosayago.tfg_iiti.model.entities.Incident

data class IncidentWithSlot(
    @Embedded val incident: Incident,

    @Relation(
        parentColumn = "slotId",
        entityColumn = "id"
    )
    val slotWithProduct: SlotWithProduct?
)