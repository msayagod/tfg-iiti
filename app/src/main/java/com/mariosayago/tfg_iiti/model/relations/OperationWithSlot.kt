package com.mariosayago.tfg_iiti.model.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.mariosayago.tfg_iiti.model.entities.Operation
import com.mariosayago.tfg_iiti.model.entities.Product
import com.mariosayago.tfg_iiti.model.entities.Slot

data class OperationWithSlot(
    @Embedded val operation: Operation,
    @Relation(
        parentColumn = "slotId",
        entityColumn = "id",
        entity = Slot::class
    )
    val slotWithProduct: SlotWithProduct
)