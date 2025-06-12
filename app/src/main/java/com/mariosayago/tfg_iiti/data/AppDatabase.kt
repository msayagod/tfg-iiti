package com.mariosayago.tfg_iiti.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mariosayago.tfg_iiti.data.dao.IncidentDao
import com.mariosayago.tfg_iiti.data.dao.MachineDao
import com.mariosayago.tfg_iiti.data.dao.OperationDao
import com.mariosayago.tfg_iiti.data.dao.ProductDao
import com.mariosayago.tfg_iiti.data.dao.SlotDao
import com.mariosayago.tfg_iiti.data.dao.VisitDao
import com.mariosayago.tfg_iiti.data.dao.VisitScheduleDao
import com.mariosayago.tfg_iiti.model.entities.*

@Database(
    entities = [
        Machine::class,
        Slot::class,
        Product::class,
        Operation::class,
        Incident::class,
        Visit::class,
        VisitSchedule::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun machineDao(): MachineDao
    abstract fun slotDao(): SlotDao
    abstract fun productDao(): ProductDao
    abstract fun operationDao(): OperationDao
    abstract fun incidentDao(): IncidentDao
    abstract fun visitDao(): VisitDao
    abstract fun visitScheduleDao(): VisitScheduleDao

    companion object {
        const val DB_NAME = "expendedoras_db"
    }
}