package com.mariosayago.tfg_iiti.data.repository

import com.mariosayago.tfg_iiti.data.dao.VisitDao
import com.mariosayago.tfg_iiti.model.entities.Visit
import com.mariosayago.tfg_iiti.model.relations.VisitWithMachine
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class VisitRepository(private val visitDao: VisitDao) {

    fun getVisitsByMachine(machineId: Long): Flow<List<Visit>> =
        visitDao.getVisitsByMachine(machineId)

    fun getVisitsWithMachineByDate(date: String): Flow<List<VisitWithMachine>> =
        visitDao.getVisitsWithMachineByDate(date)

    suspend fun insertVisit(visit: Visit): Long =
        visitDao.insertVisit(visit)

    suspend fun getVisitsInRange(from: LocalDate, to: LocalDate): List<Visit> =
        visitDao.getVisitsInRange(from.toString(), to.toString())

}
