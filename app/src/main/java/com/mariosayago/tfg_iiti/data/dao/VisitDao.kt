package com.mariosayago.tfg_iiti.data.dao

import com.mariosayago.tfg_iiti.model.entities.Visit
import com.mariosayago.tfg_iiti.model.relations.VisitWithMachine

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.mariosayago.tfg_iiti.model.relations.VisitWithOperations
import kotlinx.coroutines.flow.Flow

@Dao
interface VisitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVisit(visit: Visit): Long

    @Query("SELECT * FROM visits WHERE machineId = :machineId ORDER BY date DESC")
    fun getVisitsByMachine(machineId: Long): Flow<List<Visit>>

    @Transaction
    @Query("SELECT * FROM visits WHERE date = :date")
    fun getVisitsWithMachineByDate(date: String): Flow<List<VisitWithMachine>>

    @Query("SELECT * FROM visits WHERE date BETWEEN :start AND :end")
    suspend fun getVisitsInRange(start: String, end: String): List<Visit>

    @Transaction
    @Query("SELECT * FROM visits WHERE id = :visitId")
    fun getVisitWithOperations(visitId: Long): Flow<VisitWithOperations>

    @Transaction
    @Query("SELECT * FROM visits WHERE date = :date")
    fun getAllVisitsWithOperationsByDate(date: String): Flow<List<VisitWithOperations>>



}