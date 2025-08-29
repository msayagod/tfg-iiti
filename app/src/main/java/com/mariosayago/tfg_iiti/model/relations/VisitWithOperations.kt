package com.mariosayago.tfg_iiti.model.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.mariosayago.tfg_iiti.model.entities.Operation
import com.mariosayago.tfg_iiti.model.entities.Visit

data class VisitWithOperations(
    @Embedded val visit: Visit,
    @Relation(
        parentColumn = "id",
        entityColumn = "visitId",
        entity = Operation::class
    )
    val operations: List<Operation>
)
