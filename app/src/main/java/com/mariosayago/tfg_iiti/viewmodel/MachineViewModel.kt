package com.mariosayago.tfg_iiti.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mariosayago.tfg_iiti.data.repository.IncidentRepository
import com.mariosayago.tfg_iiti.model.entities.Machine
import com.mariosayago.tfg_iiti.model.relations.MachineWithIncidents
import com.mariosayago.tfg_iiti.model.relations.MachineWithOperations
import com.mariosayago.tfg_iiti.model.relations.MachineWithSlots
import com.mariosayago.tfg_iiti.data.repository.MachineRepository
import com.mariosayago.tfg_iiti.data.repository.ProductRepository
import com.mariosayago.tfg_iiti.data.repository.SlotRepository
import com.mariosayago.tfg_iiti.model.entities.Incident
import com.mariosayago.tfg_iiti.model.entities.Product
import com.mariosayago.tfg_iiti.model.entities.Slot
import com.mariosayago.tfg_iiti.model.relations.MachineWithSlotAndProduct
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

    init {
        viewModelScope.launch {
            // 1) Sembrar productos si no hay ninguno
            val existingProducts = productRepo.getAllProducts().first()
            val productIds = if (existingProducts.isEmpty()) {
                listOf(
                    productRepo.insertProduct(Product(name="Agua", price=1.00)),
                    productRepo.insertProduct(Product(name="Café", price=1.50)),
                    productRepo.insertProduct(Product(name="Refresco", price=1.20))
                )
            } else {
                existingProducts.map { it.id }
            }

            // 2) Sembrar máquinas si no hay ninguna
            val existingMachines = machineRepo.getAllMachines().first()
            val machineIds = if (existingMachines.isEmpty()) {
                listOf(
                    machineRepo.insertMachine(Machine(name="Máquina A", location="Edificio Principal", rows=3, columns=4)),
                    machineRepo.insertMachine(Machine(name="Máquina B", location="Planta Baja", rows=2, columns=3))
                )
            } else {
                existingMachines.map { it.id }
            }

            // 3) Sembrar slots para cada máquina si aún no tienen
            machineIds.forEach { mid ->
                val slotsForMachine = slotRepo.getSlotsByMachine(mid).first()
                if (slotsForMachine.isEmpty()) {
                    // Obtenemos la configuración de filas/columnas de la máquina
                    val machine = machineRepo.getMachineWithSlots(mid)
                        .first().machine
                    for (r in 1..machine.rows) {
                        for (c in 1..machine.columns) {
                            // Asignamos un productId aleatorio de la lista
                            val pid = productIds.random()
                            slotRepo.insertSlot(
                                Slot(
                                    machineId = mid,
                                    productId = pid,
                                    row = r,
                                    column = c,
                                    maxCapacity = 20,
                                    currentStock = 10
                                )
                            )
                        }
                    }
                }
            }

            // 4) Sembrar UNA incidencia por máquina si no tiene ninguna:
            machineIds.forEach { mid ->
                val incs = incidentRepo.getIncidentsByMachine(mid).first()
                if (incs.isEmpty()) {
                    incidentRepo.insertIncident(
                        Incident(
                            machineId   = mid,
                            date        = "2025-06-11T10:00:00",  // ISO de ejemplo
                            type        = "Stock bajo",
                            observations= "Nivel de stock menor al mínimo"
                        )
                    )
                }
            }

        }
    }

   // val machines: StateFlow<List<Machine>> = repository.getAllMachines()
     //   .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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


    fun insert(machine: Machine) {
        viewModelScope.launch {
            machineRepo.insertMachine(machine)
        }
    }

    fun update(machine: Machine) {
        viewModelScope.launch {
            machineRepo.updateMachine(machine)
        }
    }

    fun delete(machine: Machine) {
        viewModelScope.launch {
            machineRepo.deleteMachine(machine)
        }
    }
}
