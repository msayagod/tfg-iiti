package com.mariosayago.tfg_iiti.data.dao

import com.mariosayago.tfg_iiti.model.relations.OperationWithSlot

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.mariosayago.tfg_iiti.model.entities.Operation
import com.mariosayago.tfg_iiti.model.relations.OperationWithSlotAndVisit
import kotlinx.coroutines.flow.Flow

@Dao
interface OperationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOperation(operation: Operation): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(operations: List<Operation>)

    @Update
    suspend fun updateOperation(operation: Operation)


    // operaciones de un slot en la fecha dada
    @Query("""
    SELECT o.*
      FROM operations o
      JOIN visits v ON v.id = o.visitId
     WHERE o.slotId = :slotId
       AND v.date LIKE :hoy || '%'
     ORDER BY v.date DESC
""")
    fun getTodayOperationsForSlot(slotId: Long, hoy: String): Flow<List<Operation>>

    @Transaction
    @Query("""
    SELECT o.*
      FROM operations o
      JOIN slots s ON s.id = o.slotId
      JOIN visits v ON v.id = o.visitId
     WHERE s.machineId = :machineId
       AND v.date LIKE :hoy || '%'
""")
    fun getTodayOperationsWithSlotByMachine(
        machineId: Long,
        hoy: String
    ): Flow<List<OperationWithSlot>>


    @Transaction
    @Query("""
    SELECT o.*
      FROM operations o
      JOIN visits v ON v.id = o.visitId
     WHERE v.date = :date
""")
    fun getOperationsWithSlotByDate(date: String): Flow<List<OperationWithSlot>>

    @Query("SELECT * FROM operations WHERE visitId = :visitId")
    fun getOperationsByVisit(visitId: Long): Flow<List<Operation>>

    // --- Para informes ---
    @Transaction
    @Query("""
    SELECT o.*
      FROM operations o
      JOIN slots s ON s.id = o.slotId
      JOIN visits v ON v.id = o.visitId
     WHERE s.machineId = :machineId
       AND v.date LIKE :day || '%'
""")
    fun getDailyOperations(machineId: Long, day: String): Flow<List<OperationWithSlot>>

    @Transaction
    @Query("""
    SELECT o.*
      FROM operations o
      JOIN slots s ON s.id = o.slotId
      JOIN visits v ON v.id = o.visitId
     WHERE s.machineId = :machineId
       AND v.date BETWEEN :fromDate AND :toDate
     ORDER BY v.date
""")
    fun getOperationsInRange(
        machineId: Long,
        fromDate: String,
        toDate: String
    ): Flow<List<OperationWithSlot>>

    @Transaction
    @Query("""
    SELECT o.* 
      FROM operations o
      JOIN slots s ON s.id = o.slotId
      JOIN visits v ON v.id = o.visitId
     WHERE s.machineId = :machineId
       AND v.date BETWEEN :fromDate AND :toDate
     ORDER BY v.date
""")
    fun getOperationsWithSlotAndVisitInRange(
        machineId: Long,
        fromDate: String,
        toDate: String
    ): List<OperationWithSlotAndVisit>


}
