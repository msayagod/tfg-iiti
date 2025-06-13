package com.mariosayago.tfg_iiti.data.dao

import com.mariosayago.tfg_iiti.model.entities.Machine
import com.mariosayago.tfg_iiti.model.relations.MachineWithSlots
import com.mariosayago.tfg_iiti.model.relations.MachineWithOperations
import com.mariosayago.tfg_iiti.model.relations.MachineWithIncidents

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.mariosayago.tfg_iiti.model.relations.MachineWithSlotAndProduct
import kotlinx.coroutines.flow.Flow

@Dao
interface MachineDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMachine(machine: Machine): Long

    @Update
    suspend fun updateMachine(machine: Machine)

    @Delete
    suspend fun deleteMachine(machine: Machine)

    @Query("SELECT * FROM machines ORDER BY name ASC")
    fun getAllMachines(): Flow<List<Machine>>

    @Transaction
    @Query("SELECT * FROM machines WHERE id = :machineId")
    fun getMachineWithSlots(machineId: Long): Flow<MachineWithSlots>

    @Transaction
    @Query("SELECT * FROM machines WHERE id = :machineId")
    fun getMachineWithIncidents(machineId: Long): Flow<MachineWithIncidents>

    @Transaction
    @Query("SELECT * FROM machines")
    fun getMachinesWithIncidents(): Flow<List<MachineWithIncidents>>

    @Transaction
    @Query("SELECT * FROM machines WHERE id = :machineId")
    fun getMachineWithSlotAndProductList(machineId: Long): Flow<List<MachineWithSlotAndProduct>>

    @Query("SELECT * FROM machines WHERE id = :machineId")
    fun getMachineById(machineId: Long): Flow<Machine>

}