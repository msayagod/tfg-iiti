package com.mariosayago.tfg_iiti.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mariosayago.tfg_iiti.model.entities.Slot
import com.mariosayago.tfg_iiti.model.relations.SlotWithProduct
import com.mariosayago.tfg_iiti.data.repository.SlotRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
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

    fun insert(slot: Slot) {
        viewModelScope.launch { repository.insertSlot(slot) }
    }

    fun update(slot: Slot) {
        viewModelScope.launch { repository.updateSlot(slot) }
    }

    fun delete(slot: Slot) {
        viewModelScope.launch { repository.deleteSlot(slot) }
    }
}