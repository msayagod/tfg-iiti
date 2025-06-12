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

@HiltViewModel
class IncidentViewModel @Inject constructor(
    private val repository: IncidentRepository
) : ViewModel() {

    val incidents: StateFlow<List<Incident>> =
        repository.getIncidentsByMachine(/* puedes inicializar con un id por defecto o exponer un método */0L)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getIncidentsByMachine(machineId: Long): Flow<List<Incident>> =
        repository.getIncidentsByMachine(machineId)

    fun getIncidentById(incidentId: Long): Flow<Incident> =
        repository.getIncidentById(incidentId)

    fun insert(incident: Incident) {
        viewModelScope.launch { repository.insertIncident(incident) }
    }

    fun delete(incident: Incident) {
        viewModelScope.launch { repository.deleteIncident(incident) }
    }
}