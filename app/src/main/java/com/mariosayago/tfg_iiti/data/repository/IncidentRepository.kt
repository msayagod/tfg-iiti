package com.mariosayago.tfg_iiti.data.repository

import com.mariosayago.tfg_iiti.data.dao.IncidentDao
import com.mariosayago.tfg_iiti.model.entities.Incident
import kotlinx.coroutines.flow.Flow

class IncidentRepository(private val incidentDao: IncidentDao) {

    fun getIncidentsByMachine(machineId: Long): Flow<List<Incident>> =
        incidentDao.getIncidentsByMachine(machineId)

    fun getIncidentById(id: Long): Flow<Incident?> =
        incidentDao.getIncidentById(id)

    fun getOpenIncidents(): Flow<List<Incident>> =
        incidentDao.getOpenIncidents()

    fun getClosedIncidents(): Flow<List<Incident>> =
        incidentDao.getClosedIncidents()

    suspend fun updateIncident(incident: Incident) =
        incidentDao.updateIncident(incident)

    suspend fun insertIncident(incident: Incident): Long =
        incidentDao.insertIncident(incident)

    suspend fun deleteIncident(incident: Incident) =
        incidentDao.deleteIncident(incident)
}
