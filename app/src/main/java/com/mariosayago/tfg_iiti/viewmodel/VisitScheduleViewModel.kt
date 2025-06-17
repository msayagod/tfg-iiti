package com.mariosayago.tfg_iiti.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mariosayago.tfg_iiti.data.repository.MachineRepository
import com.mariosayago.tfg_iiti.model.entities.VisitSchedule
import com.mariosayago.tfg_iiti.data.repository.VisitScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject
import kotlin.math.min

@HiltViewModel
class VisitScheduleViewModel @Inject constructor(
    internal val scheduleRepo: VisitScheduleRepository,
    private val machineRepo: MachineRepository
) : ViewModel() {

    data class Ui(
        val scheduleId: Long?,
        val machineId: Long,
        val machineName: String,
        val visitDate: LocalDate,
        val nextVisitLabel: String
    )


    // Helpers para calcular las 3 próximas fechas
    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateNextDates(s: VisitSchedule): List<LocalDate> {
        val today = LocalDate.now()
        return when (s.frequency) {
            "d" -> List(3) { today.plusDays(it.toLong()) }
            "w" -> {
                // días de la semana permitidos (1=Lunes..7=Domingo)
                val days = s.daysOfWeek.split(",").map(String::toInt).sorted()
                // generamos fechas día a día hasta coleccionar 3
                generateSequence(today) { it.plusDays(1) }
                    .filter { days.contains(it.dayOfWeek.value) }
                    .take(3)
                    .toList()
            }
            "m" -> {
                val day = s.dayOfMonth ?: today.dayOfMonth
                // primera fecha de este mes (o siguiente si ya pasó)
                val first = today.withDayOfMonth(min(day, today.lengthOfMonth()))
                    .let { if (it < today) it.plusMonths(1) else it }
                List(3) { first.plusMonths(it.toLong()) }
            }
            else -> List(3) { today.plusDays(it.toLong()) }
        }
    }

    // Mapeo simple de código → texto
    private val freqLabels = mapOf(
        "d" to "diario",
        "w" to "semanal",
        "m" to "mensual"
    )

    @RequiresApi(Build.VERSION_CODES.O)
    private fun labelFor(date: LocalDate, freq: String): String {
        val period = freqLabels[freq] ?: "una vez" // una vez por defecto cuando no es d, w o m
        return when (date) {
            LocalDate.now() -> "Hoy ($period)"
            LocalDate.now().plusDays(1) -> "Mañana ($period)"
            else -> {
                // "Martes, 22/06/2025 (semanal)"
                val dayName = date.dayOfWeek
                    .getDisplayName(TextStyle.FULL, Locale("es"))
                    .replaceFirstChar { it.uppercase() }
                val formatted = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                "$dayName, $formatted ($period)"
            }
        }
    }

    private val _showAll = MutableStateFlow(false)
    val showAll = _showAll.asStateFlow()

    // 1) Combina máquinas y schedules produciendo 3 Ui por cada programación
    @RequiresApi(Build.VERSION_CODES.O)
    private val rawSchedules = combine(
        scheduleRepo.getAllSchedules(),
        machineRepo.getAllMachines()
    ) { schedules, machines ->
        schedules.flatMap { s ->
            machines.find { it.id == s.machineId }?.let { m ->
                calculateNextDates(s).map { date ->
                    Ui(
                        scheduleId     = s.id,
                        machineId      = s.machineId,
                        machineName    = m.name,
                        visitDate      = date,
                        nextVisitLabel = labelFor(date, s.frequency)
                    )
                }
            } ?: emptyList()
        }
            .sortedBy { it.visitDate }
    }


    // 2) Expone sólo 5 o todas según showAll
    @RequiresApi(Build.VERSION_CODES.O)
    val schedules: StateFlow<List<Ui>> = rawSchedules
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())


}

