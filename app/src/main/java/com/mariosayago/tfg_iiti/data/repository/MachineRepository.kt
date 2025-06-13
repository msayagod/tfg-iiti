package com.mariosayago.tfg_iiti.data.repository

import com.mariosayago.tfg_iiti.model.entities.Machine
import com.mariosayago.tfg_iiti.data.dao.MachineDao
import com.mariosayago.tfg_iiti.model.relations.MachineWithIncidents
import com.mariosayago.tfg_iiti.model.relations.MachineWithOperations
import com.mariosayago.tfg_iiti.model.relations.MachineWithSlotAndProduct
import com.mariosayago.tfg_iiti.model.relations.MachineWithSlots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MachineRepository(private val machineDao: MachineDao) {

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

    suspend fun insertMachine(machine: Machine): Long =
        machineDao.insertMachine(machine)

    suspend fun updateMachine(machine: Machine) =
        machineDao.updateMachine(machine)

    suspend fun deleteMachine(machine: Machine) =
        machineDao.deleteMachine(machine)
}
