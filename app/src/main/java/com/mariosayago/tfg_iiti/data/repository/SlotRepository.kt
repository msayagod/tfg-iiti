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
}
