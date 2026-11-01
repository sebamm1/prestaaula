package com.example.prestaaula.device

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * RECURSO DEL DISPOSITIVO 2: cámara y galería (rúbrica IE 2.4.1).
 * Las fotos se guardan dentro de la app (carpeta privada "fotos"), así no se pierden
 * y no se necesita permiso de almacenamiento. La cámara recibe un archivo a través
 * de un FileProvider (declarado en el AndroidManifest).
 */
object Fotos {

    /** Crea un archivo vacío en la carpeta privada de la app. */
    fun crearArchivo(context: Context): File {
        val carpeta = File(context.filesDir, "fotos")
        if (!carpeta.exists()) carpeta.mkdirs()
        return File(carpeta, "foto_${System.currentTimeMillis()}.jpg")
    }

    /** Uri "content://" que la app de cámara puede usar para escribir la foto. */
    fun uriPara(context: Context, archivo: File): Uri =
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)

    /** Copia una imagen elegida en la galería a la carpeta privada. Devuelve la ruta o null si falla. */
    suspend fun copiarDesdeGaleria(context: Context, uri: Uri): String? = withContext(Dispatchers.IO) {
        try {
            val destino = crearArchivo(context)
            val entrada = context.contentResolver.openInputStream(uri) ?: return@withContext null
            entrada.use { input -> destino.outputStream().use { output -> input.copyTo(output) } }
            destino.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    /** Carga una miniatura liviana de la foto (para no usar mucha memoria). */
    suspend fun cargarMiniatura(ruta: String, ladoMaximo: Int = 256): ImageBitmap? = withContext(Dispatchers.IO) {
        try {
            val opciones = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(ruta, opciones)
            var escala = 1
            while (opciones.outWidth / escala > ladoMaximo * 2 || opciones.outHeight / escala > ladoMaximo * 2) {
                escala *= 2
            }
            val opcionesFinal = BitmapFactory.Options().apply { inSampleSize = escala }
            BitmapFactory.decodeFile(ruta, opcionesFinal)?.asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }

    /** Borra un archivo de foto que ya no se usa (por ejemplo, si se canceló la cámara). */
    fun borrar(ruta: String?) {
        if (ruta == null) return
        runCatching { File(ruta).delete() }
    }
}
