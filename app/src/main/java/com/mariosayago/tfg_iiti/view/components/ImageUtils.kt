package com.mariosayago.tfg_iiti.view.components

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.IOException
import java.util.UUID

// 'Clase' para manejo de imágenes
object ImageUtils {
    /**
     * Copia el contenido de 'uri' a un fichero bajo filesDir/products/UUID.jpg
     * Devuelve la ruta absoluta del fichero copiado.
     */

    @Throws(IOException::class)
    suspend fun copyUriToInternalStorage(context: Context, uri: Uri): String {
        val input = context.contentResolver.openInputStream(uri)
            ?: throw IOException("No pude abrir $uri")
        val imagesDir = File(context.filesDir, "products").apply { mkdirs() }
        val target = File(imagesDir, "${UUID.randomUUID()}.jpg")
        input.use { ins ->
            target.outputStream().use { out ->
                ins.copyTo(out)
            }
        }
        return target.absolutePath
    }
}