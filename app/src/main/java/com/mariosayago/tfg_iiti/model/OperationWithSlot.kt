package com.mariosayago.tfg_iiti.model

import androidx.room.Embedded
import androidx.room.Relation

data class OperationWithSlot(
    @Embedded val operation: Operation,
    @Relation(
        parentColumn = "slotId",
        entityColumn = "id"
    )
    val slot: Slot
)