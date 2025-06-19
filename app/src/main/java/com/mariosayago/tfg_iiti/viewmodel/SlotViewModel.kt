package com.mariosayago.tfg_iiti.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mariosayago.tfg_iiti.model.entities.Slot
import com.mariosayago.tfg_iiti.model.relations.SlotWithProduct
import com.mariosayago.tfg_iiti.data.repository.SlotRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SlotViewModel @Inject constructor(
    private val repository: SlotRepository
) : ViewModel() {

    fun getSlotsByMachine(machineId: Long): Flow<List<Slot>> =
        repository.getSlotsByMachine(machineId)

    fun getSlotWithProduct(slotId: Long): Flow<SlotWithProduct> =
        repository.getSlotWithProduct(slotId)

    fun slotsWithProduct(machineId: Long): StateFlow<List<SlotWithProduct>> =
        repository.getSlotsWithProductByMachine(machineId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun getSlotsWithProductByMachine(machineId: Long): Flow<List<SlotWithProduct>> =
        repository.getSlotsWithProductByMachine(machineId)

    fun insert(slot: Slot) {
        viewModelScope.launch { repository.insertSlot(slot) }
    }

    fun update(slot: Slot) {
        viewModelScope.launch { repository.updateSlot(slot) }
    }

    fun updateCurrentStock(slotId: Long, stock: Int) = viewModelScope.launch {
        repository.updateCurrentStock(slotId, stock)
    }

    fun delete(slot: Slot) {
        viewModelScope.launch { repository.deleteSlot(slot) }
    }

    fun combine(slot: Slot) = viewModelScope.launch {
        repository.combineSlots(slot.machineId, slot.rowIndex, slot.colIndex)
    }

    fun uncombine(slot: Slot) = viewModelScope.launch {
        repository.uncombineSlot(slot.machineId, slot.rowIndex, slot.colIndex)
    }

}