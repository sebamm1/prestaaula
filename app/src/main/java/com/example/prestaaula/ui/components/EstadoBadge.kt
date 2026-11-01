@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.prestaaula.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.prestaaula.ui.theme.coloresExtra

/** Tipos de etiqueta: cada uno toma sus colores del tema (nunca colores fijos en la pantalla). */
enum class TipoBadge { DISPONIBLE, PRESTADO, MANTENCION, NEUTRO, ACENTO }

/** Etiqueta pequeña de estado: "Disponible", "Prestado", "En mantención", etc. */
@Composable
fun EstadoBadge(texto: String, tipo: TipoBadge, modifier: Modifier = Modifier) {
    val cs = MaterialTheme.colorScheme
    val extra = MaterialTheme.coloresExtra
    val (fondo, contenido) = when (tipo) {
        TipoBadge.DISPONIBLE -> extra.disponible to extra.onDisponible
        TipoBadge.PRESTADO -> extra.prestado to extra.onPrestado
        TipoBadge.MANTENCION -> cs.errorContainer to cs.onErrorContainer
        TipoBadge.NEUTRO -> cs.surfaceContainerHighest to cs.onSurfaceVariant
        TipoBadge.ACENTO -> cs.primaryContainer to cs.onPrimaryContainer
    }
    Surface(color = fondo, contentColor = contenido, shape = MaterialTheme.shapes.small, modifier = modifier) {
        Text(
            texto,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
