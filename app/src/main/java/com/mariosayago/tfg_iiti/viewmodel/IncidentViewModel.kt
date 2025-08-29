package com.mariosayago.tfg_iiti.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mariosayago.tfg_iiti.model.entities.Incident
import com.mariosayago.tfg_iiti.data.repository.IncidentRepository
import com.mariosayago.tfg_iiti.model.relations.IncidentWithSlotAndVisit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.firstOrNull

@HiltViewModel
class IncidentViewModel @Inject constructor(
    private val repository: IncidentRepository
) : ViewModel() {

    // Incidentes abiertos
    val openIncidents: StateFlow<List<IncidentWithSlotAndVisit>> =
        repository.getOpenIncidents()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Incidentes cerrados
    val closedIncidents: StateFlow<List<IncidentWithSlotAndVisit>> =
        repository.getClosedIncidents()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getIncidentById(id: Long): Flow<IncidentWithSlotAndVisit?> {
        return repository.getIncidentWithSlotAndVisitById(id)
    }


    fun insert(incident: Incident) {
        viewModelScope.launch { repository.insertIncident(incident) }
   }

    fun delete(incident: Incident) {
        viewModelScope.launch { repository.deleteIncident(incident) }
    }

    // 2) “cerrar” incidencia: cambiar status a “closed”
    fun closeIncident(id: Long) = viewModelScope.launch {
        repository.getIncidentById(id).firstOrNull()?.let { inc ->
            repository.updateIncident(inc.copy(status = "closed"))
        }
    }

    suspend fun getIncidentsWithSlotAndVisitInRange(
        machineId: Long,
        fromDate: String,
        toDate: String
    ): List<IncidentWithSlotAndVisit> =
        repository.getIncidentsWithSlotAndVisitInRange(machineId, fromDate, toDate)

}