package com.mariosayago.tfg_iiti.data.dao

import com.mariosayago.tfg_iiti.model.entities.Incident

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.mariosayago.tfg_iiti.model.relations.IncidentWithSlot
import kotlinx.coroutines.flow.Flow

@Dao
interface IncidentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncident(incident: Incident): Long

    @Query("SELECT * FROM incidents WHERE machineId = :machineId ORDER BY date DESC")
    fun getIncidentsByMachine(machineId: Long): Flow<List<Incident>>

    @Query("SELECT * FROM incidents WHERE id = :incidentId")
    fun getIncidentById(incidentId: Long): Flow<Incident?> // El ? es para indicar que puede ser null, sin eso daría error si no lo encontrara

    @Query("SELECT * FROM incidents WHERE status = 'open' ORDER BY date DESC")
    fun getOpenIncidents(): Flow<List<Incident>>

    @Query("SELECT * FROM incidents WHERE status = 'closed' ORDER BY date DESC")
    fun getClosedIncidents(): Flow<List<Incident>>

    @Update(onConflict = OnConflictStrategy.REPLACE) // Si hay conflictos, actualiza
    suspend fun updateIncident(incident: Incident): Int

    @Delete
    suspend fun deleteIncident(incident: Incident)

    // --- Para informes ---
    @Transaction
    @Query("""
    SELECT i.* FROM incidents i
      JOIN slots s ON s.id = i.machineId
     WHERE s.machineId = :machineId
       AND i.date LIKE :day || '%'
  """)
    fun getDailyIncidents(machineId: Long, day: String): Flow<List<IncidentWithSlot>>

    @Query("""
    SELECT *
      FROM incidents
     WHERE machineId = :machineId
       AND date BETWEEN :fromDate AND :toDate
     ORDER BY date
  """)
    fun getIncidentsInRange(
        machineId: Long,
        fromDate: String,
        toDate: String
    ): Flow<List<Incident>>

    // --- Para informes simplificados ---
    @Query(
        """
    SELECT * 
      FROM incidents 
     WHERE machineId = :machineId
       AND date BETWEEN :fromDate AND :toDate
     ORDER BY date
    """
    )
    fun getRangeIncidentsRaw(
        machineId: Long,
        fromDate: String,
        toDate: String
    ): Flow<List<Incident>>

    @Transaction
    @Query("""
    SELECT * FROM incidents 
    WHERE machineId = :machineId 
      AND date BETWEEN :fromDate AND :toDate
    ORDER BY date
""")
    fun getIncidentsWithSlotInRange(
        machineId: Long,
        fromDate: String,
        toDate: String
    ): Flow<List<IncidentWithSlot>>


}