@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.prestaaula.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.prestaaula.data.preferences.ThemeMode

/** Diálogo "Tema de la app": Claro, Oscuro o Sistema (clase "Theme Switcher"). */
@Composable
fun TemaDialog(
    modoActual: ThemeMode,
    onElegir: (ThemeMode) -> Unit,
    onCerrar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCerrar,
        icon = { Icon(Icons.Filled.Brightness6, contentDescription = null) },
        title = { Text("Tema de la app") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OpcionTema("Claro", Icons.Filled.LightMode, modoActual == ThemeMode.LIGHT) { onElegir(ThemeMode.LIGHT) }
                OpcionTema("Oscuro", Icons.Filled.DarkMode, modoActual == ThemeMode.DARK) { onElegir(ThemeMode.DARK) }
                OpcionTema("Sistema", Icons.Filled.PhoneAndroid, modoActual == ThemeMode.SYSTEM) { onElegir(ThemeMode.SYSTEM) }
            }
        },
        confirmButton = { TextButton(onClick = onCerrar) { Text("Cerrar") } }
    )
}

/** Opción reutilizable: se pinta con primaryContainer y un check cuando está seleccionada. */
@Composable
private fun OpcionTema(texto: String, icono: ImageVector, seleccionada: Boolean, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val fondo by animateColorAsState(if (seleccionada) cs.primaryContainer else cs.surfaceContainer, label = "fondoOpcion")
    val contenido = if (seleccionada) cs.onPrimaryContainer else cs.onSurface
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(fondo)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(icono, contentDescription = null, tint = contenido)
        Text(texto, style = MaterialTheme.typography.bodyLarge, color = contenido, modifier = Modifier.weight(1f))
        if (seleccionada) Icon(Icons.Filled.Check, contentDescription = "Seleccionado", tint = contenido)
    }
}
