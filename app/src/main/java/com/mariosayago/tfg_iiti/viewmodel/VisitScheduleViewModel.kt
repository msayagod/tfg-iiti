package com.mariosayago.tfg_iiti.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mariosayago.tfg_iiti.data.repository.MachineRepository
import com.mariosayago.tfg_iiti.model.entities.VisitSchedule
import com.mariosayago.tfg_iiti.data.repository.VisitScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class VisitScheduleViewModel @Inject constructor(
    private val scheduleRepo: VisitScheduleRepository,
    private val machineRepo: MachineRepository
) : ViewModel() {

    // Modelo UI para la pantalla
    data class Ui(
        val scheduleId: Long,
        val machineId: Long,
        val machineName: String,
        val nextVisit: String
    )

    // Estado que exponen las visitas ordenadas por próxima fecha
    val schedules: StateFlow<List<Ui>> = combine(
        scheduleRepo.getAllSchedules(),
        machineRepo.getAllMachines()
    ) { schedules, machines ->
        // Aseguramos que haya datos de ejemplo
        if (schedules.isEmpty() && machines.isNotEmpty()) {
            // Sembramos un horario semanal a las 09:00 para cada máquina
            machines.forEach { m ->
                scheduleRepo.insertSchedule(
                    VisitSchedule(
                        machineId   = m.id,
                        frequency   = "w",          // weekly
                        daysOfWeek  = "1",          // lunes
                        hour        = "09:00"
                    )
                )
            }
        }
        // Volvemos a leer tras posible semilla
        scheduleRepo.getAllSchedules().first().mapNotNull { s ->
            val machine = machines.find { it.id == s.machineId } ?: return@mapNotNull null
            Ui(
                scheduleId  = s.id,
                machineId   = s.machineId,
                machineName = machine.name,
                nextVisit   = "${s.daysOfWeek.replace("1","Lun")} ${s.hour ?: "--:--"}"
            )
        }.sortedBy { it.nextVisit }
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        // Lanzamos la carga / semilla una vez al arrancar
        viewModelScope.launch { schedules.first() }
    }
}
