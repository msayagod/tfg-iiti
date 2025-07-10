package com.mariosayago.tfg_iiti.data.dao

import com.mariosayago.tfg_iiti.model.relations.OperationWithSlot

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.mariosayago.tfg_iiti.model.entities.Operation
import kotlinx.coroutines.flow.Flow

@Dao
interface OperationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOperation(operation: Operation): Long

    @Update
    suspend fun updateOperation(operation: Operation)

    @Query("SELECT * FROM operations WHERE slotId = :slotId ORDER BY date DESC")
    fun getOperationsForSlot(slotId: Long): Flow<List<Operation>>

    // operaciones de un slot en la fecha dada
    @Query(
        """
  SELECT * 
    FROM operations
   WHERE slotId = :slotId
     AND date LIKE :hoy || '%'
   ORDER BY date DESC
"""
    )
    fun getTodayOperationsForSlot(slotId: Long, hoy: String): Flow<List<Operation>>

    @Transaction
    @Query(
        """
    SELECT o.* 
      FROM operations o
      JOIN slots s    ON s.id        = o.slotId
     WHERE s.machineId = :machineId
       AND o.date LIKE :hoy || '%'
  """
    )
    fun getTodayOperationsWithSlotByMachine(
        machineId: Long,
        hoy: String
    ): Flow<List<OperationWithSlot>>


    @Transaction
    @Query("SELECT * FROM operations WHERE date = :date")
    fun getOperationsWithSlotByDate(date: String): Flow<List<OperationWithSlot>>

    // --- Para informes ---
    @Transaction
    @Query(
        """
    SELECT o.* FROM operations o
      JOIN slots s ON s.id = o.slotId
     WHERE s.machineId = :machineId
       AND o.date LIKE :day || '%'
  """
    )
    fun getDailyOperations(machineId: Long, day: String): Flow<List<OperationWithSlot>>

    @Transaction
    @Query("""
    SELECT * 
      FROM operations o
      JOIN slots s ON s.id = o.slotId
     WHERE s.machineId = :machineId
       AND o.date BETWEEN :fromDate AND :toDate
     ORDER BY o.date
  """)
    fun getOperationsInRange(
        machineId: Long,
        fromDate: String,
        toDate: String
    ): Flow<List<OperationWithSlot>>
}
