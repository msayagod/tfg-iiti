package com.mariosayago.tfg_iiti.model.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.mariosayago.tfg_iiti.model.entities.Machine
import com.mariosayago.tfg_iiti.model.entities.Operation
import com.mariosayago.tfg_iiti.model.entities.Slot

data class MachineWithOperations(
    @Relation(
        entity = Operation::class,
        parentColumn = "id",            // machine.id
        entityColumn = "slotId",        // Operation.slotId → Slot.id
        associateBy = Junction(
            value = Slot::class,
            parentColumn = "machineId",   // Slot.machineId → machine.id
            entityColumn = "id"           // Slot.id → Operation.slotId
        )
    )
    val operations: List<Operation>
)
