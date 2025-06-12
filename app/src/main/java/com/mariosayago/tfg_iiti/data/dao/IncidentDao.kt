package com.mariosayago.tfg_iiti.data.dao

import com.mariosayago.tfg_iiti.model.entities.Incident

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface IncidentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncident(incident: Incident): Long

    @Query("SELECT * FROM incidents WHERE machineId = :machineId ORDER BY date DESC")
    fun getIncidentsByMachine(machineId: Long): Flow<List<Incident>>

    @Query("SELECT * FROM incidents WHERE id = :incidentId")
    fun getIncidentById(incidentId: Long): Flow<Incident>

    @Delete
    suspend fun deleteIncident(incident: Incident)
}