package com.mariosayago.tfg_iiti.data.dao

import com.mariosayago.tfg_iiti.model.entities.Operation
import com.mariosayago.tfg_iiti.model.relations.OperationWithSlot

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface OperationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOperation(operation: Operation): Long

    @Query("SELECT * FROM operations WHERE slotId = :slotId ORDER BY date DESC")
    fun getOperationsForSlot(slotId: Long): Flow<List<Operation>>

    @Transaction
    @Query("SELECT * FROM operations WHERE date = :date")
    fun getOperationsWithSlotByDate(date: String): Flow<List<OperationWithSlot>>
}