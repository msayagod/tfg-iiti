package com.mariosayago.tfg_iiti.model.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.mariosayago.tfg_iiti.model.entities.Product
import com.mariosayago.tfg_iiti.model.entities.Slot

data class SlotWithProduct(
    @Embedded val slot: Slot,
    @Relation(
        parentColumn = "productId",
        entityColumn = "id"
    )
    val product: Product?
)