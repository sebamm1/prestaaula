@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.prestaaula.ui.components

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.prestaaula.device.Fotos
import com.example.prestaaula.ui.theme.coloresExtra
import kotlinx.coroutines.launch

/**
 * RECURSO DEL DISPOSITIVO: cámara y galería (rúbrica IE 2.4.1).
 * - "Tomar foto": pide el permiso de cámara (acceso seguro) y abre la cámara.
 * - "Galería": abre el selector de fotos de Android (no necesita permiso).
 * Si algo falla (sin cámara, permiso denegado), muestra un mensaje y la app sigue funcionando.
 *
 * @param origen "camara" o "galeria" (para el texto bajo "Foto adjunta").
 */
@Composable
fun FotoSelector(
    rutaFoto: String?,
    origen: String?,
    onFoto: (ruta: String, origen: String) -> Unit,
    onQuitar: () -> Unit,
    onMensaje: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    // Ruta del archivo que la cámara está llenando (se guarda por si Android recrea la pantalla)
    var rutaPendiente by rememberSaveable { mutableStateOf<String?>(null) }

    val tomarFoto = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { exito ->
        val ruta = rutaPendiente
        if (exito && ruta != null) onFoto(ruta, "camara") else Fotos.borrar(ruta)
        rutaPendiente = null
    }

    fun abrirCamara() {
        val archivo = Fotos.crearArchivo(context)
        rutaPendiente = archivo.absolutePath
        try {
            tomarFoto.launch(Fotos.uriPara(context, archivo))
        } catch (e: ActivityNotFoundException) {
            rutaPendiente = null
            onMensaje("Este dispositivo no tiene una app de cámara")
        } catch (e: Exception) {
            rutaPendiente = null
            onMensaje("No se pudo abrir la cámara")
        }
    }

    val pedirPermiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        if (concedido) abrirCamara()
        else onMensaje("Sin permiso no se puede usar la cámara. Puedes elegir una foto de la galería")
    }

    val galeria = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            scope.launch {
                val ruta = Fotos.copiarDesdeGaleria(context, uri)
                if (ruta != null) onFoto(ruta, "galeria") else onMensaje("No se pudo cargar la foto")
            }
        }
    }

    if (rutaFoto != null) {
        val miniatura by produceState<ImageBitmap?>(initialValue = null, rutaFoto) {
            value = Fotos.cargarMiniatura(rutaFoto)
        }
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            border = BorderStroke(1.dp, MaterialTheme.coloresExtra.exito),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    val imagen = miniatura
                    if (imagen != null) {
                        Image(
                            bitmap = imagen,
                            contentDescription = "Foto adjunta",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            if (origen == "camara") Icons.Filled.PhotoCamera else Icons.Filled.PhotoLibrary,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Foto adjunta", style = MaterialTheme.typography.titleSmall)
                    Text(
                        if (origen == "camara") "Tomada con la cámara" else "Elegida de la galería",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onQuitar) {
                    Icon(Icons.Filled.Close, contentDescription = "Quitar foto")
                }
            }
        }
    } else {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = {
                    val tienePermiso = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                        PackageManager.PERMISSION_GRANTED
                    if (tienePermiso) abrirCamara() else pedirPermiso.launch(Manifest.permission.CAMERA)
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Filled.PhotoCamera, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Tomar foto")
            }
            OutlinedButton(
                onClick = { galeria.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Filled.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Galería")
            }
        }
    }
}
