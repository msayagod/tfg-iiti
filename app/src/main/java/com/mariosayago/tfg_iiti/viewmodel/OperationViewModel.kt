package com.mariosayago.tfg_iiti.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
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

    fun todayOpsForSlot(slotId: Long, hoy: String) =
        repository.getTodayOperationsForSlot(slotId, hoy)

    fun todayOpsWithSlotByMachine(machineId: Long, hoy: String) =
        repository.getTodayOperationsWithSlotByMachine(machineId, hoy)

    @Insert(onConflict = REPLACE) // Si la operación ya existe, la reemplaza (sirve como update)
    fun insert(operation: Operation) {
        viewModelScope.launch { repository.insertOperation(operation) }
    }
}