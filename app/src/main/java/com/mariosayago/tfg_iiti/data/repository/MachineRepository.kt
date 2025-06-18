package com.mariosayago.tfg_iiti.data.repository

import com.mariosayago.tfg_iiti.model.entities.Machine
import com.mariosayago.tfg_iiti.data.dao.MachineDao
import com.mariosayago.tfg_iiti.data.dao.SlotDao
import com.mariosayago.tfg_iiti.model.entities.Slot
import com.mariosayago.tfg_iiti.model.relations.MachineWithIncidents
import com.mariosayago.tfg_iiti.model.relations.MachineWithOperations
import com.mariosayago.tfg_iiti.model.relations.MachineWithSlotAndProduct
import com.mariosayago.tfg_iiti.model.relations.MachineWithSlots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MachineRepository @Inject constructor(
    private val machineDao: MachineDao,
    private val slotDao: SlotDao
) {

    fun getAllMachines(): Flow<List<Machine>> = machineDao.getAllMachines()

    fun getMachineWithSlots(machineId: Long): Flow<MachineWithSlots> =
        machineDao.getMachineWithSlots(machineId)

    fun getMachineWithIncidents(machineId: Long): Flow<MachineWithIncidents> =
        machineDao.getMachineWithIncidents(machineId)

    fun getMachinesWithIncidents(): Flow<List<MachineWithIncidents>> =
        machineDao.getMachinesWithIncidents()

    fun getMachineWithSlotAndProductList(machineId: Long): Flow<List<MachineWithSlotAndProduct>> =
        machineDao.getMachineWithSlotAndProductList(machineId)

    fun getMachineById(id: Long): Flow<Machine> =
        machineDao.getMachineById(id)

    /**
     * Inserta la máquina y genera todos sus slots vacíos.
     * @return el ID de la máquina recién insertada
     */
    suspend fun insertMachineWithEmptySlots(machine: Machine): Long {
        // 1) Inserto la máquina
        val machineId = machineDao.insertMachine(machine)

        // 2) Genero huecos vacíos
        for (r in 1..machine.rows) {
            for (c in 1..machine.columns) {
                slotDao.insertSlot(
                    Slot(
                        machineId    = machineId,
                        productId    = null,     // <-- sin producto
                        rowIndex     = r,
                        colIndex     = c,
                        maxCapacity  = 0,        // o tu valor por defecto
                        currentStock = 0,        // siempre 0 al crear
                        combinedWithNext = false
                    )
                )
            }
        }
        return machineId
    }

    suspend fun updateMachineAndSlots(machine: Machine, oldRows: Int, oldCols: Int) {
        machineDao.updateMachine(machine)
        if (oldRows!=machine.rows || oldCols!=machine.columns) {
            slotDao.deleteSlotsByMachine(machine.id)
            insertMachineWithEmptySlots(machine.copy(id=machine.id))
        }
    }

    suspend fun insertMachine(machine: Machine): Long =
        machineDao.insertMachine(machine)

    suspend fun updateMachine(machine: Machine) =
        machineDao.updateMachine(machine)

    suspend fun deleteMachine(machine: Machine) =
        machineDao.deleteMachine(machine)
}
