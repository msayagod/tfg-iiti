package com.mariosayago.tfg_iiti.data.dao

import com.mariosayago.tfg_iiti.model.entities.Slot
import com.mariosayago.tfg_iiti.model.relations.SlotWithProduct

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SlotDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSlot(slot: Slot): Long

    @Update
    suspend fun updateSlot(slot: Slot)

    @Delete
    suspend fun deleteSlot(slot: Slot)

    @Query("SELECT * FROM slots WHERE machineId = :machineId ORDER BY row, column")
    fun getSlotsByMachine(machineId: Long): Flow<List<Slot>>

    @Transaction
    @Query("SELECT * FROM slots WHERE id = :slotId")
    fun getSlotWithProduct(slotId: Long): Flow<SlotWithProduct>
}