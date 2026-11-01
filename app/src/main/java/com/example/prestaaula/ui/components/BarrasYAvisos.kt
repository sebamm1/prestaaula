@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.prestaaula.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/** Pestañas del Docente (barra inferior): Inventario y Mis reservas. */
enum class PestanaDocente { INVENTARIO, MIS_RESERVAS }

@Composable
fun DocenteBottomBar(
    actual: PestanaDocente,
    onInventario: () -> Unit,
    onMisReservas: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val colores = NavigationBarItemDefaults.colors(
        indicatorColor = cs.primaryContainer,
        selectedIconColor = cs.onPrimaryContainer
    )
    NavigationBar {
        NavigationBarItem(
            selected = actual == PestanaDocente.INVENTARIO,
            onClick = onInventario,
            icon = { Icon(Icons.Filled.Inventory2, contentDescription = null) },
            label = { Text("Inventario") },
            colors = colores
        )
        NavigationBarItem(
            selected = actual == PestanaDocente.MIS_RESERVAS,
            onClick = onMisReservas,
            icon = { Icon(Icons.Filled.Event, contentDescription = null) },
            label = { Text("Mis reservas") },
            colors = colores
        )
    }
}

/**
 * Aviso visual "Vibración": aparece arriba cada vez que el teléfono vibra.
 * Necesario porque el emulador no vibra (así se ve que el recurso se usó).
 * Se muestra cuando cambia "contador".
 */
@Composable
fun AvisoVibracion(contador: Int, modifier: Modifier = Modifier) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(contador) {
        if (contador > 0) {
            visible = true
            delay(900)
            visible = false
        }
    }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically { altura -> -altura / 2 },
        exit = fadeOut(),
        modifier = modifier
    ) {
        Surface(
            color = MaterialTheme.colorScheme.inverseSurface,
            contentColor = MaterialTheme.colorScheme.inverseOnSurface,
            shape = CircleShape,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Filled.Vibration, contentDescription = null, modifier = Modifier.size(16.dp))
                Text("Vibración", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}
