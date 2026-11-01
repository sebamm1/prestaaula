@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.prestaaula.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material.icons.filled.TabletAndroid
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.prestaaula.ui.theme.coloresExtra

/** Ícono según el tipo de equipo. */
fun iconoTipo(tipo: String): ImageVector = when (tipo) {
    "Notebook" -> Icons.Filled.Laptop
    "Proyector" -> Icons.Filled.Slideshow
    "Tablet" -> Icons.Filled.TabletAndroid
    "Cable" -> Icons.Filled.Cable
    else -> Icons.Filled.Category
}

/** Cuadro de color con un ícono (tarjetas de equipo y de menú). */
@Composable
fun TileIcono(icono: ImageVector, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
    }
}

/** Título de cada paso de un formulario: "1. ¿Para qué curso?" */
@Composable
fun TituloPaso(texto: String, nota: String? = null, modifier: Modifier = Modifier) {
    Row(modifier = modifier.padding(top = 16.dp, bottom = 6.dp), verticalAlignment = Alignment.Bottom) {
        Text(texto, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
        if (nota != null) {
            Text(
                " ($nota)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/** Texto de ayuda pequeño (explicaciones y listas vacías). */
@Composable
fun TextoAyuda(texto: String, modifier: Modifier = Modifier, centrado: Boolean = false) {
    Text(
        texto,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = if (centrado) TextAlign.Center else TextAlign.Start,
        modifier = modifier.fillMaxWidth()
    )
}

/**
 * Animación "sacudir": el botón se mueve de lado a lado cuando el formulario tiene errores
 * (retroalimentación visual, rúbrica IE 2.2.2). Se dispara cada vez que cambia "disparo".
 */
@Composable
fun Modifier.sacudir(disparo: Int): Modifier {
    val inicial = remember { disparo }
    val desplazamiento = remember { Animatable(0f) }
    LaunchedEffect(disparo) {
        if (disparo != inicial) {
            for (x in listOf(-18f, 18f, -12f, 12f, -5f, 0f)) {
                desplazamiento.animateTo(x, animationSpec = tween(durationMillis = 45))
            }
        }
    }
    return this.graphicsLayer { translationX = desplazamiento.value }
}

/** Botón principal de ancho completo, con ícono opcional y animación de sacudir. */
@Composable
fun BotonPrincipal(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icono: ImageVector? = null,
    sacudir: Int = 0,
    habilitado: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = habilitado,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
            .height(48.dp)
            .sacudir(sacudir)
    ) {
        if (icono != null) {
            Icon(icono, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(texto)
    }
}

/**
 * Fila seleccionable (bloques horarios y préstamos a devolver).
 * Libre: ícono de círculo. Elegida: check con color de acento. Ocupada: rojo con ✕.
 */
@Composable
fun OpcionFila(
    titulo: String,
    subtitulo: String,
    seleccionada: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    ocupada: Boolean = false,
    textoDerecha: String? = null,
    adicional: (@Composable () -> Unit)? = null
) {
    val cs = MaterialTheme.colorScheme
    val fondo by animateColorAsState(
        when {
            ocupada -> cs.errorContainer
            seleccionada -> cs.primaryContainer
            else -> cs.surfaceContainerLow
        },
        label = "fondoOpcion"
    )
    val contenido = when {
        ocupada -> cs.onErrorContainer
        seleccionada -> cs.onPrimaryContainer
        else -> cs.onSurface
    }
    val icono = when {
        ocupada -> Icons.Filled.Cancel
        seleccionada -> Icons.Filled.CheckCircle
        else -> Icons.Filled.RadioButtonUnchecked
    }
    val resaltado = ocupada || seleccionada
    Surface(
        onClick = onClick,
        color = fondo,
        contentColor = contenido,
        shape = MaterialTheme.shapes.medium,
        border = if (ocupada && seleccionada) BorderStroke(2.dp, cs.error) else null,
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(icono, contentDescription = null, tint = if (resaltado) contenido else cs.onSurfaceVariant)
            Column(modifier = Modifier.weight(1f)) {
                Text(titulo, style = MaterialTheme.typography.titleSmall)
                Text(
                    subtitulo,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (resaltado) contenido else cs.onSurfaceVariant
                )
                if (adicional != null) adicional()
            }
            if (textoDerecha != null) {
                Text(
                    textoDerecha,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (resaltado) contenido else MaterialTheme.coloresExtra.exito,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

/** Tarjeta de menú del Encargado(a): ícono, título, descripción, contador opcional y flecha. */
@Composable
fun TarjetaMenu(
    titulo: String,
    descripcion: String,
    icono: ImageVector,
    onClick: () -> Unit,
    contador: Int = 0
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            TileIcono(icono)
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(titulo, style = MaterialTheme.typography.titleMedium)
                    if (contador > 0) {
                        Badge(modifier = Modifier.padding(start = 8.dp)) { Text("$contador") }
                    }
                }
                Text(
                    descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
