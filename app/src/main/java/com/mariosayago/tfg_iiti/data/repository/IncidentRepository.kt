package com.mariosayago.tfg_iiti.data.repository

import com.mariosayago.tfg_iiti.data.dao.IncidentDao
import com.mariosayago.tfg_iiti.model.entities.Incident
import com.mariosayago.tfg_iiti.model.relations.IncidentWithSlotAndVisit
import kotlinx.coroutines.flow.Flow

class IncidentRepository(private val incidentDao: IncidentDao) {

    fun getIncidentById(id: Long): Flow<Incident?> =
        incidentDao.getIncidentById(id)

    fun getIncidentWithSlotAndVisitById(id: Long): Flow<IncidentWithSlotAndVisit?> {
        return incidentDao.getIncidentWithSlotAndVisitById(id)
    }


    fun getOpenIncidents(): Flow<List<IncidentWithSlotAndVisit>> =
        incidentDao.getOpenIncidents()

    fun getClosedIncidents(): Flow<List<IncidentWithSlotAndVisit>> =
        incidentDao.getClosedIncidents()

    suspend fun insertIncident(incident: Incident): Long =
        incidentDao.insertIncident(incident)

    suspend fun updateIncident(incident: Incident) =
        incidentDao.updateIncident(incident)

    suspend fun deleteIncident(incident: Incident) =
        incidentDao.deleteIncident(incident)

    suspend fun getIncidentsWithSlotAndVisitInRange(
        machineId: Long,
        fromDate: String,
        toDate: String
    ): List<IncidentWithSlotAndVisit> =
        incidentDao.getIncidentsWithSlotAndVisitInRange(machineId, fromDate, toDate)

}
