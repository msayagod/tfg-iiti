package com.mariosayago.tfg_iiti.view.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mariosayago.tfg_iiti.model.entities.Incident
import com.mariosayago.tfg_iiti.model.relations.OperationWithSlot
import com.mariosayago.tfg_iiti.view.components.createReportPdf
import com.mariosayago.tfg_iiti.viewmodel.MachineViewModel
import com.mariosayago.tfg_iiti.viewmodel.ReportViewModel
import kotlinx.coroutines.flow.map
import com.mariosayago.tfg_iiti.model.relations.IncidentWithSlot


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReportDetailScreen(
    machineId: Long,
    fromDate: String,
    toDate: String,
    onExportPdf: () -> Unit,
    reportVm: ReportViewModel = hiltViewModel(),
    machineVm: MachineViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // 1) Recuperamos el nombre de la máquina
    val machine by machineVm.machines
        .map { list -> list.firstOrNull { it.id == machineId } }
        .collectAsState(initial = null)
    val machineName = machine?.name.orEmpty()

    // 2) Cargamos el informe en el ViewModel
    LaunchedEffect(machineId, fromDate, toDate) {
        reportVm.loadReport(machineId, fromDate, toDate)
    }

    // 3) Observamos listas de operaciones e incidencias
    val ops by reportVm.operations.collectAsState(initial = emptyList<OperationWithSlot>())
    val incs by reportVm.incidents.collectAsState(initial = emptyList<IncidentWithSlot>())


    // 4) Lanzador para “CreateDocument” (elige carpeta+nombre .pdf)
    val createPdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        uri?.let {
            // Reutilizamos tu función de creación de PDF, pasándole la URI destino
            context.createReportPdf(
                machineName     = machineName,
                fromDate        = fromDate,
                toDate          = toDate,
                operations      = ops,
                incidents       = incs,
                targetUri       = it,
                contentResolver = context.contentResolver
            )
        }
    }

    // 5) UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Informe: $machineName", style = MaterialTheme.typography.titleLarge)

        // Operaciones
        Text("Operaciones", style = MaterialTheme.typography.titleMedium)
        if (ops.isEmpty()) {
            Text("– Ninguna operación –")
        } else {
            ops.forEach { opWithSlot ->
                val slot = opWithSlot.slotWithProduct.slot
                val op   = opWithSlot.operation
                Text("Slot ${slot.rowIndex}-${slot.colIndex} • ${op.date} • rep: ${op.replenishedUnits}")
            }
        }

        Spacer(Modifier.height(16.dp))

        // Incidencias
        Text("Incidencias", style = MaterialTheme.typography.titleMedium)
        if (incs.isEmpty()) {
            Text("– Ninguna incidencia –")
        } else {
            incs.forEach { incWithSlot ->
                val inc = incWithSlot.incident
                val slot = incWithSlot.slotWithProduct.slot
                Text("Slot ${slot.rowIndex}-${slot.colIndex} • ${inc.date} • ${inc.observations.orEmpty()}")
            }


        }

        Spacer(Modifier.height(32.dp))

        // Botón de exportar
        Button(
            onClick = { createPdfLauncher.launch("informe_${fromDate}_${toDate}.pdf") },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Exportar PDF")
        }
    }
}







