package com.mariosayago.tfg_iiti.data.repository

import com.mariosayago.tfg_iiti.data.dao.SlotDao
import com.mariosayago.tfg_iiti.model.entities.Slot
import com.mariosayago.tfg_iiti.model.relations.SlotWithProduct
import kotlinx.coroutines.flow.Flow

class SlotRepository(private val slotDao: SlotDao) {

    fun getSlotsByMachine(machineId: Long): Flow<List<Slot>> =
        slotDao.getSlotsByMachine(machineId)

    fun getSlotWithProduct(slotId: Long): Flow<SlotWithProduct> =
        slotDao.getSlotWithProduct(slotId)

    fun getSlotsWithProductByMachine(machineId: Long): Flow<List<SlotWithProduct>> =
        slotDao.getSlotsWithProductByMachine(machineId)

    suspend fun insertSlot(slot: Slot): Long =
        slotDao.insertSlot(slot)

    suspend fun updateSlot(slot: Slot) =
        slotDao.updateSlot(slot)

    suspend fun deleteSlot(slot: Slot) =
        slotDao.deleteSlot(slot)

    suspend fun deleteSlotsByMachine(machineId: Long) =
        slotDao.deleteSlotsByMachine(machineId)


    // Combinar huecos
    suspend fun combineSlots(
        machineId: Long,
        row: Int,
        column: Int
    ) {
        val first = slotDao.getSlotAt(machineId, row, column) ?: return
        val second = slotDao.getSlotAt(machineId, row, column + 1) ?: return

        // Marcar el primero como combinado (ocupará dos columnas)
        val updatedFirst = first.copy(
            combinedWithNext = true,
            // podrías incrementar maxCapacity o currentStock si quieres…
        )
        slotDao.updateSlot(updatedFirst)

        // Eliminar el segundo hueco por completo
        slotDao.deleteSlot(second)
    }


    // Descombinar huecos
    suspend fun uncombineSlot(machineId: Long, row: Int, column: Int) {
        val first = slotDao.getSlotAt(machineId, row, column) ?: return

        // Desmarcar el combinado
        val resetFirst = first.copy(combinedWithNext = false)
        slotDao.updateSlot(resetFirst)

        // Recrear el hueco derecho en su posición original
        slotDao.insertSlot(
            first.copy(
                id = 0L, // para que Room genere un nuevo PK
                colIndex = column + 1,
                combinedWithNext = false,
                productId = null,
                currentStock = 0
            )
        )
    }
}
