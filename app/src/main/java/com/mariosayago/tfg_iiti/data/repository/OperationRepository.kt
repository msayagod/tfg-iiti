package com.mariosayago.tfg_iiti.data.repository

import com.mariosayago.tfg_iiti.data.dao.OperationDao
import com.mariosayago.tfg_iiti.model.entities.Operation
import com.mariosayago.tfg_iiti.model.relations.OperationWithSlot
import kotlinx.coroutines.flow.Flow

class OperationRepository(private val operationDao: OperationDao) {

    fun getOperationsForSlot(slotId: Long): Flow<List<Operation>> =
        operationDao.getOperationsForSlot(slotId)

    fun getTodayOperationsForSlot(slotId: Long, hoy: String) =
        operationDao.getTodayOperationsForSlot(slotId, hoy)

    fun getTodayOperationsWithSlotByMachine(machineId: Long, hoy: String) =
        operationDao.getTodayOperationsWithSlotByMachine(machineId, hoy)


    fun getOperationsWithSlotByDate(date: String): Flow<List<OperationWithSlot>> =
        operationDao.getOperationsWithSlotByDate(date)

    suspend fun insertOperation(operation: Operation): Long =
        operationDao.insertOperation(operation)

    suspend fun updateOperation(op: Operation) = operationDao.updateOperation(op)
}
