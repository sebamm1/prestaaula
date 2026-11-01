@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.prestaaula.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextOverflow

/**
 * Barra superior de PrestaAula.
 * - Usa el color de acento del rol (primaryContainer).
 * - onVolver: muestra la flecha de volver si no es null.
 * - onSalir: si no es null, muestra el menú ⋮ con "Tema de la app" y "Salir".
 *   Si es null (login), muestra solo el botón de tema.
 * - plana: true en login y elección de rol (sin color de fondo).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrestaTopBar(
    titulo: String,
    onTema: () -> Unit,
    onVolver: (() -> Unit)? = null,
    onSalir: (() -> Unit)? = null,
    plana: Boolean = false
) {
    var menuAbierto by remember { mutableStateOf(false) }
    val cs = MaterialTheme.colorScheme
    val fondo = if (plana) cs.background else cs.primaryContainer
    val contenido = if (plana) cs.onBackground else cs.onPrimaryContainer

    TopAppBar(
        title = { Text(titulo, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        navigationIcon = {
            if (onVolver != null) {
                IconButton(onClick = onVolver) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                }
            }
        },
        actions = {
            if (onSalir == null) {
                IconButton(onClick = onTema) {
                    Icon(Icons.Filled.Brightness6, contentDescription = "Tema de la app")
                }
            } else {
                Box {
                    IconButton(onClick = { menuAbierto = true }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "Más opciones")
                    }
                    DropdownMenu(expanded = menuAbierto, onDismissRequest = { menuAbierto = false }) {
                        DropdownMenuItem(
                            text = { Text("Tema de la app") },
                            leadingIcon = { Icon(Icons.Filled.Brightness6, contentDescription = null) },
                            onClick = { menuAbierto = false; onTema() }
                        )
                        DropdownMenuItem(
                            text = { Text("Salir") },
                            leadingIcon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null) },
                            onClick = { menuAbierto = false; onSalir() }
                        )
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = fondo,
            titleContentColor = contenido,
            navigationIconContentColor = contenido,
            actionIconContentColor = contenido
        )
    )
}
