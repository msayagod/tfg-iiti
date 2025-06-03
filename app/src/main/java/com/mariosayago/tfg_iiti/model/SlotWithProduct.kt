package com.mariosayago.tfg_iiti.model

import androidx.room.Embedded
import androidx.room.Relation

data class SlotWithProduct(
    @Embedded val slot: Slot,
    @Relation(
        parentColumn = "productId",
        entityColumn = "id"
    )
    val product: Product?
)