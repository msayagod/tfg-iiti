package com.mariosayago.tfg_iiti.data.dao

import com.mariosayago.tfg_iiti.model.entities.VisitSchedule

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VisitScheduleDao {

    @Query("SELECT * FROM visit_schedules")
    fun getAllSchedules(): Flow<List<VisitSchedule>>

    @Query("SELECT * FROM visit_schedules WHERE machineId = :machineId")
    fun getSchedulesForMachine(machineId: Long): Flow<List<VisitSchedule>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: VisitSchedule): Long

    @Query("DELETE FROM visit_schedules WHERE id = :scheduleId")
    suspend fun deleteSchedule(scheduleId: Long)

    @Query("SELECT * FROM visit_schedules WHERE machineId = :mid")
    suspend fun getSchedulesByMachine(mid: Long): List<VisitSchedule>

    @Query("DELETE FROM visit_schedules WHERE machineId = :mid")
    suspend fun deleteByMachine(mid: Long)



}