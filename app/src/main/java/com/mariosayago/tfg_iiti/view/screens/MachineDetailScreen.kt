package com.mariosayago.tfg_iiti.view.screens

import androidx.benchmark.perfetto.ExperimentalPerfettoTraceProcessorApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mariosayago.tfg_iiti.viewmodel.MachineViewModel
import com.mariosayago.tfg_iiti.viewmodel.SlotViewModel
import kotlin.math.min


import androidx.compose.foundation.background

import androidx.compose.foundation.verticalScroll


import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign

import coil.compose.AsyncImage
import kotlin.math.abs

@OptIn(ExperimentalPerfettoTraceProcessorApi::class)
@Composable
fun MachineDetailScreen(
    machineId: Long,
    onEditClick: (Long) -> Unit,
    onDeleteClick: (Long) -> Unit,
    onEditSlotsClick: (Long) -> Unit,
    onNewIncidentClick: (Long) -> Unit,
    onOperateMachine: (Long) -> Unit,
    slotVm: SlotViewModel = hiltViewModel(),
    viewModel: MachineViewModel = hiltViewModel()
) {
    // 1) Cargamos la máquina con slots y productos
    val list by viewModel
        .getMachineWithSlotAndProductList(machineId)
        .collectAsState(initial = emptyList())

    // 2) Mientras no llega data, mostramos "Cargando…"
    if (list.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Cargando…")
        }
        return
    }

    // 3) Tenemos la data
    val mwsp = list.first()
    var showGrid by rememberSaveable { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    var showDeleteConfirm by rememberSaveable { mutableStateOf(false) }
    val primaryColor = MaterialTheme.colorScheme.primary

    // 7) Estado de selección
    val selectedIds = remember { mutableStateListOf<Long>() }
    LaunchedEffect(mwsp.slots.map { it.slot.id }) {
        selectedIds.clear()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // ─── 1. Fila de botones de acción ───────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { onEditClick(machineId) }) { Text("Editar") }
            Button(onClick = { onEditSlotsClick(machineId) }) { Text("Editar slots") }
            Button(onClick = { onNewIncidentClick(machineId) }) { Text("Nueva incidencia") }
            Button(onClick = { onOperateMachine(machineId) }) { Text("Operar máquina") }
        }


        HorizontalDivider(thickness = 1.dp)

        // ─── 3. Toggle grid / eliminar ──────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = { showDeleteConfirm = true }) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar máquina",
                    tint = MaterialTheme.colorScheme.error
                )
            }
            IconButton(onClick = { showGrid = !showGrid }) {
                Icon(
                    imageVector = if (showGrid) Icons.AutoMirrored.Filled.ViewList else Icons.Default.ViewModule,
                    contentDescription = if (showGrid) "Ver lista" else "Ver rejilla"
                )
            }
        }

        // ─── 4. Confirmación de eliminación ─────────────────────
        if (showDeleteConfirm) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = false },
                title = { Text("Confirmar eliminación") },
                text = { Text("¿Seguro que quieres eliminar esta máquina?") },
                confirmButton = {
                    TextButton(onClick = {
                        showDeleteConfirm = false
                        onDeleteClick(machineId)
                    }) {
                        Text("Eliminar", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirm = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        // ─── 5. Datos de la máquina ─────────────────────────────
        Text(mwsp.machine.name, style = MaterialTheme.typography.titleLarge)
        Text("Ubicación: ${mwsp.machine.location}")

        // ─── 6. Sección de slots ────────────────────────────────
        Text("Slots:", style = MaterialTheme.typography.titleMedium)

        if (!showGrid) {
            // Lista vertical con scroll y icono pequeño
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(mwsp.slots) { sp ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        sp.product?.imagePath?.let { url ->
                            AsyncImage(
                                model = url,
                                contentDescription = sp.product.name,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(Modifier.width(8.dp))
                        }
                        Text(
                            "Slot ${sp.slot.rowIndex}-${sp.slot.colIndex}: " +
                                    "${sp.slot.currentStock} uds. de ${sp.product?.name ?: "—"}"
                        )
                    }
                    HorizontalDivider()
                }
            }
        } else {
            // Rejilla con paginación si hay >6 columnas
            val slots = mwsp.slots
            val totalCols = mwsp.machine.columns
            val pageSize = if (totalCols > 6) (totalCols + 1) / 2 else totalCols
            val pages = (totalCols + pageSize - 1) / pageSize
            var page by rememberSaveable { mutableStateOf(0) }

            val startCol = page * pageSize + 1
            val endCol = min(totalCols, startCol + pageSize - 1)

            // Navegación página
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { if (page > 0) page-- }, enabled = page > 0) {
                    Icon(Icons.AutoMirrored.Filled.ArrowLeft, contentDescription = "Anterior")
                }
                Text("Página ${page + 1} / $pages", style = MaterialTheme.typography.bodySmall)
                IconButton(onClick = { if (page < pages - 1) page++ }, enabled = page < pages - 1) {
                    Icon(Icons.AutoMirrored.Filled.ArrowRight, contentDescription = "Siguiente")
                }
            }

            Spacer(Modifier.height(4.dp))


            // ─── Cabecera de columnas ─────────────────────────────
            Row(Modifier.fillMaxWidth()) {
                Text(
                    "", // espacio para la etiqueta de fila
                    modifier = Modifier.width(24.dp)
                )
                for (col in startCol..endCol) {
                    Text(
                        col.toString(),
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 4.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // ─── Cuerpo de la rejilla con etiquetas de fila (falta la etiqueta de fila)
            Column(
                Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp) // ajusta si quieres scroll interno
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val rowsCount = mwsp.machine.rows
                for (rowIdx in 1..rowsCount) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Etiqueta de fila
                        Text(
                            rowIdx.toString(),
                            modifier = Modifier
                                .width(24.dp)
                                .padding(vertical = 8.dp),
                            textAlign = TextAlign.Center
                        )
                        // Celdas de esa fila
                        for (col in startCol..endCol) {
                            val sp = slots.firstOrNull {
                                it.slot.rowIndex == rowIdx && it.slot.colIndex == col
                            }
                            if (sp != null) {
                                // span si combinado
                                val span = if (sp.slot.combinedWithNext) 2 else 1
                                // border si seleccionado
                                val isSelected = selectedIds.contains(sp.slot.id)
                                val border = if (isSelected) {
                                    Modifier.border(
                                        2.dp,
                                        primaryColor,
                                        RoundedCornerShape(4.dp)
                                    )
                                } else Modifier


                                Card(
                                    modifier = border
                                        .weight(span.toFloat())
                                        .aspectRatio(span.toFloat())
                                        .clickable {
                                            val id = sp.slot.id
                                            when {
                                                // 1) Si ya está seleccionado, lo quitamos
                                                id in selectedIds -> selectedIds.remove(id)
                                                // 2) Si no hay ninguno seleccionado, lo añadimos
                                                selectedIds.isEmpty() -> selectedIds.add(id)
                                                // 3) Si ya hay uno, permitimos el segundo solo si está contiguo en la misma fila
                                                selectedIds.size == 1 -> {
                                                    val firstSlot = slots.first { it.slot.id == selectedIds[0] }.slot
                                                    if (
                                                        firstSlot.rowIndex == sp.slot.rowIndex &&
                                                        abs(firstSlot.colIndex - sp.slot.colIndex) == 1 &&
                                                        !firstSlot.combinedWithNext &&
                                                        !sp.slot.combinedWithNext
                                                    ) {
                                                        selectedIds.add(id)
                                                    }
                                                }
                                                // 4) Si ya hay dos, no hacemos nada
                                                else -> { /* nada */ }
                                            }
                                        },
                                    elevation = CardDefaults.cardElevation(4.dp)
                                ) {
                                    Box {
                                        // Imagen de fondo
                                        sp.product?.imagePath.let { url ->
                                            AsyncImage(
                                                model = url,
                                                contentDescription = sp.product?.name,
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                        Box(
                                            Modifier
                                                .fillMaxSize()
                                                .background(
                                                    MaterialTheme.colorScheme.onSurface.copy(
                                                        alpha = 0.3f
                                                    )
                                                )
                                        )
                                        Column(
                                            Modifier
                                                .fillMaxSize()
                                                .padding(8.dp),
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                "${sp.slot.currentStock}",
                                                style = MaterialTheme.typography.titleMedium,
                                                color = MaterialTheme.colorScheme.surface
                                            )
                                            Text(
                                                sp.product?.name ?: "—",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.surface
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

                    // ─── Botones Combinar / Descombinar ───────────────────────
                    val sorted = selectedIds
                        .mapNotNull { id -> slots.firstOrNull { it.slot.id == id }?.slot }
                        .sortedBy { it.colIndex }

                    val canCombine = sorted.size == 2 &&
                            sorted[0].rowIndex == sorted[1].rowIndex &&
                            sorted[1].colIndex - sorted[0].colIndex == 1 &&
                            !sorted[0].combinedWithNext &&
                            !sorted[1].combinedWithNext

                    val canUncombine = selectedIds.size == 1 &&
                            slots.first { it.slot.id == selectedIds[0] }.slot.combinedWithNext

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                slotVm.combine(sorted[0])         // ← slotVm sigue usándose
                                selectedIds.clear()
                            },
                            enabled = canCombine,
                            modifier = Modifier.weight(1f)
                        ) { Text("Combinar") }
                        Button(
                            onClick = {
                                slotVm.uncombine(
                                    slots.first { it.slot.id == selectedIds[0] }.slot
                                )
                                selectedIds.clear()
                            },
                            enabled = canUncombine,
                            modifier = Modifier.weight(1f)
                        ) { Text("Descombinar") }
                    }
                }
            }
        }






