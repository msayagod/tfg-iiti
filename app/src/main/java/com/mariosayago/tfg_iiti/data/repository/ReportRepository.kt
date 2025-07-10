package com.mariosayago.tfg_iiti.data.repository

import com.mariosayago.tfg_iiti.data.dao.IncidentDao
import com.mariosayago.tfg_iiti.data.dao.OperationDao
import com.mariosayago.tfg_iiti.model.relations.IncidentWithSlot
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ReportRepository @Inject constructor(
    private val opDao: OperationDao,
    private val incDao: IncidentDao
) {
    fun rangeOperations(
        machineId: Long, from: String, to: String
    ) = opDao.getOperationsInRange(machineId, from, to)

    fun rangeIncidents(
        machineId: Long, from: String, to: String
    ): Flow<List<IncidentWithSlot>> =
        incDao.getIncidentsWithSlotInRange(machineId, from, to)
}
