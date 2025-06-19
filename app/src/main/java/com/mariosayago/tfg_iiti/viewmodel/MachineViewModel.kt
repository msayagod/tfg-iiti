package com.mariosayago.tfg_iiti.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mariosayago.tfg_iiti.data.repository.IncidentRepository
import com.mariosayago.tfg_iiti.model.entities.Machine
import com.mariosayago.tfg_iiti.model.relations.MachineWithIncidents
import com.mariosayago.tfg_iiti.model.relations.MachineWithSlots
import com.mariosayago.tfg_iiti.data.repository.MachineRepository
import com.mariosayago.tfg_iiti.data.repository.ProductRepository
import com.mariosayago.tfg_iiti.data.repository.SlotRepository
import com.mariosayago.tfg_iiti.model.entities.Slot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MachineViewModel @Inject constructor(
    private val machineRepo: MachineRepository,
    private val productRepo: ProductRepository,
    private val slotRepo: SlotRepository,
    private val incidentRepo: IncidentRepository
) : ViewModel() {

    // Estado de la lista
    val machines: StateFlow<List<Machine>> = machineRepo.getAllMachines()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    fun getMachineWithSlots(machineId: Long): Flow<MachineWithSlots> =
        machineRepo.getMachineWithSlots(machineId)

    fun getMachineWithIncidents(machineId: Long): Flow<MachineWithIncidents> =
        machineRepo.getMachineWithIncidents(machineId)

    fun machinesWithIncidents(): StateFlow<List<MachineWithIncidents>> =
        machineRepo.getMachinesWithIncidents()
            .map { list -> list.filter { it.incidents.isNotEmpty() } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun getMachineWithSlotAndProductList(machineId: Long) =
        machineRepo.getMachineWithSlotAndProductList(machineId)


    // Funcion insert que inserta una máquina y sus slots
    fun insertAndSeed(
        machine: Machine,
        defaultSlotCapacity: Int,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            val newId = machineRepo.insertMachine(machine)
            seedSlotsFor(newId, machine.rows, machine.columns, defaultSlotCapacity)
            // cuando termina la siembra, llamamos al callback
            onComplete()
        }
    }


    fun updateMachine(machine: Machine, defaultSlotCapacity: Int, old: Machine, onDone: ()->Unit) {
        viewModelScope.launch {
            machineRepo.updateMachine(machine)
            if (old.rows != machine.rows ||
                old.columns != machine.columns
            ) {
                slotRepo.deleteSlotsByMachine(machine.id)
                seedSlotsFor(machine.id, machine.rows, machine.columns, defaultSlotCapacity)
            }
            onDone()
        }
    }

    fun delete(machine: Machine) {
        viewModelScope.launch {
            machineRepo.deleteMachine(machine)
        }
    }

    fun getMachineById(id: Long): Flow<Machine> =
        machineRepo.getMachineById(id)


    private suspend fun seedSlotsFor(
        machineId: Long,
        rows: Int,
        columns: Int,
        slotCapacity: Int
    ) {
        try {
            val products = productRepo.getAllProducts().first()
            for (r in 1..rows) {
                for (c in 1..columns) {
                    slotRepo.insertSlot(
                        Slot(
                            machineId = machineId,
                            productId = null, //No asignar productos al crear slots
                            rowIndex = r,
                            colIndex = c,
                            maxCapacity = slotCapacity,
                            currentStock = 0
                        )
                    )

                }
            }

    // Recoger excepcion en caso de error
        } catch (e: Exception) {
            Log.e("SEED", "¡ERROR en seedSlotsFor!", e)
        }
    }
}


