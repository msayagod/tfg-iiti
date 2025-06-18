package com.mariosayago.tfg_iiti.di

import android.content.Context
import androidx.room.Room
import com.mariosayago.tfg_iiti.data.AppDatabase
import com.mariosayago.tfg_iiti.data.dao.IncidentDao
import com.mariosayago.tfg_iiti.data.dao.MachineDao
import com.mariosayago.tfg_iiti.data.dao.OperationDao
import com.mariosayago.tfg_iiti.data.dao.ProductDao
import com.mariosayago.tfg_iiti.data.dao.SlotDao
import com.mariosayago.tfg_iiti.data.dao.VisitDao
import com.mariosayago.tfg_iiti.data.dao.VisitScheduleDao
import com.mariosayago.tfg_iiti.data.repository.IncidentRepository
import com.mariosayago.tfg_iiti.data.repository.MachineRepository
import com.mariosayago.tfg_iiti.data.repository.OperationRepository
import com.mariosayago.tfg_iiti.data.repository.ProductRepository
import com.mariosayago.tfg_iiti.data.repository.SlotRepository
import com.mariosayago.tfg_iiti.data.repository.VisitRepository
import com.mariosayago.tfg_iiti.data.repository.VisitScheduleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /*** BASE DE DATOS ***/
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, AppDatabase.DB_NAME) // Nombre de la base de datos
            .fallbackToDestructiveMigration(true) // Permite que se elimine la base de datos y se vuelva a crear
            .build()

    /*** DAOs ***/
    @Provides fun provideMachineDao(db: AppDatabase): MachineDao = db.machineDao()
    @Provides fun provideSlotDao(db: AppDatabase): SlotDao = db.slotDao()
    @Provides fun provideProductDao(db: AppDatabase): ProductDao = db.productDao()
    @Provides fun provideOperationDao(db: AppDatabase): OperationDao = db.operationDao()
    @Provides fun provideIncidentDao(db: AppDatabase): IncidentDao = db.incidentDao()
    @Provides fun provideVisitDao(db: AppDatabase): VisitDao = db.visitDao()
    @Provides fun provideVisitScheduleDao(db: AppDatabase): VisitScheduleDao = db.visitScheduleDao()

    /*** REPOSITORIES ***/
    @Provides @Singleton
    fun provideMachineRepository(dao: MachineDao, dao2: SlotDao): MachineRepository =
        MachineRepository(dao, dao2)

    @Provides @Singleton
    fun provideSlotRepository(dao: SlotDao): SlotRepository =
        SlotRepository(dao)

    @Provides @Singleton
    fun provideProductRepository(dao: ProductDao): ProductRepository =
        ProductRepository(dao)

    @Provides @Singleton
    fun provideOperationRepository(dao: OperationDao): OperationRepository =
        OperationRepository(dao)

    @Provides @Singleton
    fun provideIncidentRepository(dao: IncidentDao): IncidentRepository =
        IncidentRepository(dao)

    @Provides @Singleton
    fun provideVisitRepository(dao: VisitDao): VisitRepository =
        VisitRepository(dao)

    @Provides @Singleton
    fun provideVisitScheduleRepository(dao: VisitScheduleDao): VisitScheduleRepository =
        VisitScheduleRepository(dao)
}
