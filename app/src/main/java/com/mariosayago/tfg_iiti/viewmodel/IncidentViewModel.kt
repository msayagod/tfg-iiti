package com.mariosayago.tfg_iiti.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mariosayago.tfg_iiti.model.entities.Incident
import com.mariosayago.tfg_iiti.data.repository.IncidentRepository
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

    val incidents: StateFlow<List<Incident>> =
        repository.getIncidentsByMachine(0L) // se puede inicializar con un id por defecto o exponer un metodo
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getIncidentsByMachine(machineId: Long): Flow<List<Incident>> =
        repository.getIncidentsByMachine(machineId)

    // Incidentes abiertos
    val openIncidents: StateFlow<List<Incident>> =
        repository.getOpenIncidents()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Incidentes cerrados
    val closedIncidents: StateFlow<List<Incident>> =
        repository.getClosedIncidents()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // 2) “cerrar” incidencia: cambiar status a “closed”
    fun closeIncident(id: Long) = viewModelScope.launch {
        repository.getIncidentById(id).firstOrNull()?.let { inc ->
            repository.updateIncident(inc.copy(status = "closed"))
        }
    }
    fun getIncidentById(incidentId: Long): Flow<Incident?> =
        repository.getIncidentById(incidentId)

    fun insert(incident: Incident) {
        viewModelScope.launch { repository.insertIncident(incident) }
   }

    fun delete(incident: Incident) {
        viewModelScope.launch { repository.deleteIncident(incident) }
    }
}