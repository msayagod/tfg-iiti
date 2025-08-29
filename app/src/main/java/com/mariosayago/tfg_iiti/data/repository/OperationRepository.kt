package com.mariosayago.tfg_iiti.data.repository

import com.mariosayago.tfg_iiti.data.dao.OperationDao
import com.mariosayago.tfg_iiti.model.entities.Operation
import com.mariosayago.tfg_iiti.model.relations.OperationWithSlot
import com.mariosayago.tfg_iiti.model.relations.OperationWithSlotAndVisit
import kotlinx.coroutines.flow.Flow

class OperationRepository(private val operationDao: OperationDao) {


    fun getTodayOperationsForSlot(slotId: Long, hoy: String) =
        operationDao.getTodayOperationsForSlot(slotId, hoy)

    fun getTodayOperationsWithSlotByMachine(machineId: Long, hoy: String) =
        operationDao.getTodayOperationsWithSlotByMachine(machineId, hoy)


    fun getOperationsWithSlotByDate(date: String): Flow<List<OperationWithSlot>> =
        operationDao.getOperationsWithSlotByDate(date)

    suspend fun insertOperation(operation: Operation): Long =
        operationDao.insertOperation(operation)

    suspend fun insertAll(operations: List<Operation>) =
        operationDao.insertAll(operations)

    suspend fun updateOperation(op: Operation) = operationDao.updateOperation(op)

    suspend fun getOperationsWithSlotAndVisitInRange(
        machineId: Long,
        fromDate: String,
        toDate: String
    ): List<OperationWithSlotAndVisit> =
        operationDao.getOperationsWithSlotAndVisitInRange(machineId, fromDate, toDate)

}
