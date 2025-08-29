package com.mariosayago.tfg_iiti.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mariosayago.tfg_iiti.data.repository.IncidentRepository
import com.mariosayago.tfg_iiti.data.repository.OperationRepository
import com.mariosayago.tfg_iiti.model.entities.Visit
import com.mariosayago.tfg_iiti.model.relations.VisitWithMachine
import com.mariosayago.tfg_iiti.data.repository.VisitRepository
import com.mariosayago.tfg_iiti.model.entities.Incident
import com.mariosayago.tfg_iiti.model.entities.Operation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VisitViewModel @Inject constructor(
    private val visitRepository: VisitRepository,
    private val operationRepository: OperationRepository,
    private val incidentRepository: IncidentRepository
) : ViewModel() {

    fun getVisitsByMachine(machineId: Long): Flow<List<Visit>> =
        visitRepository.getVisitsByMachine(machineId)

    fun getVisitsWithMachineByDate(date: String): Flow<List<VisitWithMachine>> =
        visitRepository.getVisitsWithMachineByDate(date)

    fun insert(visit: Visit) {
        viewModelScope.launch { visitRepository.insertVisit(visit) }
    }

    fun insertVisitWithOperations(visit: Visit, operations: List<Operation>) {
        viewModelScope.launch {
            val visitId = visitRepository.insertVisit(visit)
            operations.forEach {
                val op = it.copy(visitId = visitId)
                operationRepository.insertOperation(op)
            }
        }
    }

    fun insertVisitWithOperationsAndIncidents(
        visit: Visit,
        operations: List<Operation>,
        incidents: List<Incident>
    ) {
        viewModelScope.launch {
            // 1. Insertamos la visita y obtenemos su ID
            val visitId = visitRepository.insertVisit(visit)

            // 2. Insertamos las operaciones (no llevan visitId, pero sí están vinculadas a slots)
            operations.forEach { operation ->
                operationRepository.insertOperation(operation)
            }

            // 3. Insertamos las incidencias, asignándoles el visitId
            incidents.forEach { incident ->
                val incidentWithVisitId = incident.copy(visitId = visitId)
                incidentRepository.insertIncident(incidentWithVisitId)
            }
        }
    }
}