package com.mariosayago.tfg_iiti.data.dao

import com.mariosayago.tfg_iiti.model.entities.Incident

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.mariosayago.tfg_iiti.model.relations.IncidentWithSlotAndVisit
import kotlinx.coroutines.flow.Flow

@Dao
interface IncidentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncident(incident: Incident): Long

    @Query("SELECT * FROM incidents WHERE id = :incidentId")
    fun getIncidentById(incidentId: Long): Flow<Incident?> // El ? es para indicar que puede ser null, sin eso daría error si no lo encontrara

    @Transaction
    @Query("SELECT * FROM incidents WHERE id = :incidentId")
    fun getIncidentWithSlotAndVisitById(incidentId: Long): Flow<IncidentWithSlotAndVisit?>

    @Transaction
    @Query("""
    SELECT i.*
      FROM incidents i
      JOIN visits v ON v.id = i.visitId
     WHERE i.status = 'open'
     ORDER BY v.date DESC
""")
    fun getOpenIncidents(): Flow<List<IncidentWithSlotAndVisit>>


    @Transaction
    @Query("""
    SELECT i.*
      FROM incidents i
      JOIN visits v ON v.id = i.visitId
     WHERE i.status = 'closed'
     ORDER BY v.date DESC
""")
    fun getClosedIncidents(): Flow<List<IncidentWithSlotAndVisit>>


    @Update(onConflict = OnConflictStrategy.REPLACE) // Si hay conflictos, actualiza
    suspend fun updateIncident(incident: Incident): Int

    @Delete
    suspend fun deleteIncident(incident: Incident)

    // --- Para informes ---


    // --- Para informes simplificados ---



    @Transaction
    @Query("""
    SELECT i.*
      FROM incidents i
      JOIN visits v ON v.id = i.visitId
     WHERE i.machineId = :machineId
       AND v.date BETWEEN :fromDate AND :toDate
     ORDER BY v.date
""")

    fun getIncidentsWithSlotAndVisitInRange(
        machineId: Long,
        fromDate: String,
        toDate: String
    ): List<IncidentWithSlotAndVisit>



}