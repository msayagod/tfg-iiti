package com.mariosayago.tfg_iiti.model.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.mariosayago.tfg_iiti.model.entities.Incident
import com.mariosayago.tfg_iiti.model.entities.Slot

data class IncidentWithSlot(
    @Embedded val incident: Incident,
    @Relation(
        parentColumn = "machineId",        // asumimos que Incident tiene machineId
        entityColumn = "machineId",        // o, si tu esquema usaba slotId, ajusta aquí
        entity = Slot::class
    )
    val slotWithProduct: SlotWithProduct
)