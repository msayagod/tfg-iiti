package com.mariosayago.tfg_iiti.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "machines")
data class Machine (
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val location: String,
    val rows: Int,
    val columns: Int

)