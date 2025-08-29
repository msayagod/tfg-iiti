package com.mariosayago.tfg_iiti.view.components

import android.content.ContentResolver
import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import com.mariosayago.tfg_iiti.model.relations.OperationWithSlotAndVisit
import com.mariosayago.tfg_iiti.model.relations.IncidentWithSlotAndVisit

/**
 * Crea un informe en PDF con las operaciones e incidencias y lo guarda en la URI indicada.
 *
 * @param machineName Nombre de la máquina para el encabezado.
 * @param fromDate    Fecha de inicio (YYYY-MM-DD).
 * @param toDate      Fecha final   (YYYY-MM-DD).
 * @param operations  Lista de operaciones (con slot embebido).
 * @param incidents   Lista de incidencias (con slot embebido).
 * @param targetUri   URI donde escribir el PDF (obtenida de CreateDocument).
 * @param contentResolver El content resolver para abrir el stream.
 */
fun Context.createReportPdf(
    machineName: String,
    fromDate: String,
    toDate: String,
    operations: List<OperationWithSlotAndVisit>,
    incidents: List<IncidentWithSlotAndVisit>,
    targetUri: Uri,
    contentResolver: ContentResolver
) {
    // 1) Creamos el documento y la primera página
    val document = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    var page = document.startPage(pageInfo)
    val canvas = page.canvas
    val paint = Paint().apply { textSize = 12f }

    // 2) Encabezados
    canvas.drawText("Informe: $machineName", 20f, 30f, paint)
    canvas.drawText("Desde $fromDate  Hasta $toDate", 20f, 50f, paint)

    // 3) Dibujamos las operaciones
    var y = 80f
    canvas.drawText("Operaciones:", 20f, y, paint)
    y += 20f
    operations.forEach { opWithSlot ->
        val slot = opWithSlot.slotWithProduct.slot
        val date = opWithSlot.visit.date
        val units = opWithSlot.operation.replenishedUnits
        val line = "Slot ${slot.rowIndex}-${slot.colIndex} | Fecha: $date | Repuestos: $units"

        canvas.drawText(line, 20f, y, paint)
        y += 20f

        // Si nos acercamos al final de la página, creamos una nueva
        if (y > pageInfo.pageHeight - 40) {
            document.finishPage(page)
            page = document.startPage(pageInfo)
            y = 20f
        }
    }

    // 4) Dibujamos las incidencias
    if (incidents.isNotEmpty()) {
        y += 10f
        canvas.drawText("Incidencias:", 20f, y, paint)
        y += 20f
        incidents.forEach { incWithSlot ->
            val slot = incWithSlot.slotWithProduct?.slot
            val slotDesc = slot?.let { "Slot ${it.rowIndex}-${it.colIndex} | " } ?: ""
            val date = incWithSlot.visit.date
            val text = "${slotDesc}Fecha: $date | ${incWithSlot.incident.observations}"
            canvas.drawText(text, 20f, y, paint)
            y += 20f

            if (y > pageInfo.pageHeight - 40) {
                document.finishPage(page)
                page = document.startPage(pageInfo)
                y = 20f
            }
        }
    }

    // 5) Finalizamos la última página
    document.finishPage(page)

    // 6) Abrimos el OutputStream sobre la URI seleccionada y escribimos el PDF
    contentResolver.openOutputStream(targetUri)?.use { out ->
        document.writeTo(out)
    }

    // 7) Cerramos el documento
    document.close()
}


