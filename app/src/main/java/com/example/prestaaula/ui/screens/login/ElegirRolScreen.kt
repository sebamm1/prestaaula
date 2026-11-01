@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.prestaaula.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.prestaaula.data.local.entity.UsuarioEntity
import com.example.prestaaula.data.local.entity.listaRoles
import com.example.prestaaula.model.Rol
import com.example.prestaaula.ui.components.PrestaTopBar
import com.example.prestaaula.ui.theme.PrestaAulaTheme

/** Solo para usuarios con más de un rol (ej: Andrea, UTP y Docente). Cada tarjeta lleva el color de su rol. */
@Composable
fun ElegirRolScreen(
    usuario: UsuarioEntity,
    onTema: () -> Unit,
    onSalir: () -> Unit,
    onElegir: (Rol) -> Unit
) {
    Scaffold(
        topBar = {
            PrestaTopBar(
                titulo = "Hola, ${usuario.nombre.substringBefore(" ")}",
                onTema = onTema,
                onSalir = onSalir,
                plana = true
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                "Tienes más de un rol. Elige cómo quieres entrar.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            val oscuro = MaterialTheme.colorScheme.background.luminance() < 0.5f
            usuario.listaRoles().forEach { rol ->
                // Cada tarjeta usa el color de acento de su rol
                PrestaAulaTheme(darkTheme = oscuro, rol = rol) {
                    TarjetaRol(rol = rol, onClick = { onElegir(rol) })
                }
            }
        }
    }
}

private fun textosRol(rol: Rol): Triple<String, String, ImageVector> = when (rol) {
    Rol.DOCENTE -> Triple("Entrar como Docente", "Ver equipos disponibles y reservar", Icons.Filled.School)
    Rol.ENCARGADO -> Triple("Entrar como Encargado(a) CRA", "Aprobar reservas, recibir y revisar equipos", Icons.Filled.Inventory2)
    Rol.UTP -> Triple("Entrar como UTP / Administración", "Ver reportes de uso e incidencias", Icons.Filled.BarChart)
}

@Composable
private fun TarjetaRol(rol: Rol, onClick: () -> Unit) {
    val (titulo, descripcion, icono) = textosRol(rol)
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        shape = MaterialTheme.shapes.large,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
            }
            Column {
                Text(titulo, style = MaterialTheme.typography.titleMedium)
                Text(descripcion, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
