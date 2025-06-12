package com.mariosayago.tfg_iiti.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mariosayago.tfg_iiti.model.entities.Visit
import com.mariosayago.tfg_iiti.model.relations.VisitWithMachine
import com.mariosayago.tfg_iiti.data.repository.VisitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VisitViewModel @Inject constructor(
    private val repository: VisitRepository
) : ViewModel() {

    fun getVisitsByMachine(machineId: Long): Flow<List<Visit>> =
        repository.getVisitsByMachine(machineId)

    fun getVisitsWithMachineByDate(date: String): Flow<List<VisitWithMachine>> =
        repository.getVisitsWithMachineByDate(date)

    fun insert(visit: Visit) {
        viewModelScope.launch { repository.insertVisit(visit) }
    }
}