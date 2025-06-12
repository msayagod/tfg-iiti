package com.mariosayago.tfg_iiti.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mariosayago.tfg_iiti.model.entities.Operation
import com.mariosayago.tfg_iiti.model.relations.OperationWithSlot
import com.mariosayago.tfg_iiti.data.repository.OperationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OperationViewModel @Inject constructor(
    private val repository: OperationRepository
) : ViewModel() {

    fun getOperationsForSlot(slotId: Long): Flow<List<Operation>> =
        repository.getOperationsForSlot(slotId)

    fun getOperationsWithSlotByDate(date: String): Flow<List<OperationWithSlot>> =
        repository.getOperationsWithSlotByDate(date)

    fun insert(operation: Operation) {
        viewModelScope.launch { repository.insertOperation(operation) }
    }
}