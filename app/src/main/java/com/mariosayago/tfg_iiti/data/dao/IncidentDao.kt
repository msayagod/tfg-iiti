package com.mariosayago.tfg_iiti.data.dao

import com.mariosayago.tfg_iiti.model.entities.Incident

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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
}