package com.mariosayago.tfg_iiti.data.repository

import com.mariosayago.tfg_iiti.data.dao.VisitScheduleDao
import com.mariosayago.tfg_iiti.model.entities.VisitSchedule
import kotlinx.coroutines.flow.Flow

class VisitScheduleRepository(private val scheduleDao: VisitScheduleDao) {

    fun getAllSchedules(): Flow<List<VisitSchedule>> = scheduleDao.getAllSchedules()

    fun getSchedulesForMachine(machineId: Long): Flow<List<VisitSchedule>> =
        scheduleDao.getSchedulesForMachine(machineId)

    suspend fun insertSchedule(schedule: VisitSchedule): Long =
        scheduleDao.insertSchedule(schedule)

    suspend fun deleteSchedule(scheduleId: Long) =
        scheduleDao.deleteSchedule(scheduleId)
}
