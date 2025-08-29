package com.mariosayago.tfg_iiti.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mariosayago.tfg_iiti.data.repository.ReportRepository
import com.mariosayago.tfg_iiti.model.relations.IncidentWithSlot
import com.mariosayago.tfg_iiti.model.relations.IncidentWithSlotAndVisit
import com.mariosayago.tfg_iiti.model.relations.OperationWithSlotAndVisit
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val repo: ReportRepository
) : ViewModel() {
    private val _params = MutableStateFlow<Triple<Long, String, String>?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val operations = _params
        .flatMapLatest { p ->
            if (p == null) emptyFlow()
            else flow { emit(repo.rangeOperations(p.first, p.second, p.third)) }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList<OperationWithSlotAndVisit>())

    @OptIn(ExperimentalCoroutinesApi::class)
    val incidents = _params
        .flatMapLatest { p ->
            if (p == null) emptyFlow()
            else flow { emit(repo.rangeIncidents(p.first, p.second, p.third)) }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList<IncidentWithSlotAndVisit>())

    fun loadReport(machineId: Long, from: String, to: String) {
        _params.value = Triple(machineId, from, to)
    }
}

