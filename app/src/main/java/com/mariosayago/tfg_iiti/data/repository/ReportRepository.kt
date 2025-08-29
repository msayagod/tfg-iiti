package com.mariosayago.tfg_iiti.data.repository

import com.mariosayago.tfg_iiti.data.dao.IncidentDao
import com.mariosayago.tfg_iiti.data.dao.OperationDao
import com.mariosayago.tfg_iiti.model.relations.IncidentWithSlotAndVisit
import com.mariosayago.tfg_iiti.model.relations.OperationWithSlotAndVisit
import javax.inject.Inject

class ReportRepository @Inject constructor(
    private val opDao: OperationDao,
    private val incDao: IncidentDao
) {
    fun rangeOperations(machineId: Long, fromDate: String, toDate: String): List<OperationWithSlotAndVisit> {
        return opDao.getOperationsWithSlotAndVisitInRange(machineId, fromDate, toDate)
    }

    fun rangeIncidents(machineId: Long, fromDate: String, toDate: String): List<IncidentWithSlotAndVisit> {
        return incDao.getIncidentsWithSlotAndVisitInRange(machineId, fromDate, toDate)
    }

}
