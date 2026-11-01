@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.prestaaula.ui.screens.docente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.prestaaula.data.local.entity.EquipoEntity
import com.example.prestaaula.model.EstadoEquipo
import com.example.prestaaula.model.TIPOS_EQUIPO
import com.example.prestaaula.ui.components.BarraBusqueda
import com.example.prestaaula.ui.components.DocenteBottomBar
import com.example.prestaaula.ui.components.EstadoBadge
import com.example.prestaaula.ui.components.FiltroTipo
import com.example.prestaaula.ui.components.PestanaDocente
import com.example.prestaaula.ui.components.PrestaTopBar
import com.example.prestaaula.ui.components.TextoAyuda
import com.example.prestaaula.ui.components.TileIcono
import com.example.prestaaula.ui.components.TipoBadge
import com.example.prestaaula.ui.components.iconoTipo
import com.example.prestaaula.viewmodel.AppViewModelProvider
import com.example.prestaaula.viewmodel.FiltroInventario
import com.example.prestaaula.viewmodel.InventarioViewModel

fun tipoBadge(estado: String): TipoBadge = when (estado) {
    EstadoEquipo.DISPONIBLE -> TipoBadge.DISPONIBLE
    EstadoEquipo.PRESTADO -> TipoBadge.PRESTADO
    else -> TipoBadge.MANTENCION
}

/**
 * Inventario. La misma pantalla sirve para:
 * - Docente (modoEncargado = false): "¿Qué equipo necesitas?", toca un equipo para reservarlo.
 * - Encargado(a) (modoEncargado = true): estado de todos los equipos y "Marcar como reparado".
 * Lista con LazyColumn y una Card por equipo (Semana 5).
 */
@Composable
fun InventarioScreen(
    modoEncargado: Boolean,
    onTema: () -> Unit,
    onSalir: () -> Unit,
    onVolver: (() -> Unit)?,
    onReservar: (Int) -> Unit = {},
    onMisReservas: () -> Unit = {},
    onMensaje: (String) -> Unit = {},
    viewModel: InventarioViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val filtro by viewModel.filtro.collectAsState()
    val equipos by viewModel.equipos.collectAsState()

    Scaffold(
        topBar = { PrestaTopBar(titulo = "Inventario", onTema = onTema, onVolver = onVolver, onSalir = onSalir) },
        bottomBar = {
            if (!modoEncargado) {
                DocenteBottomBar(PestanaDocente.INVENTARIO, onInventario = {}, onMisReservas = onMisReservas)
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Text(
                    if (modoEncargado) "Estado de todos los equipos" else "¿Qué equipo necesitas?",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                BarraBusqueda(
                    texto = filtro.busqueda,
                    onCambio = viewModel::cambiarBusqueda,
                    sugerencia = "Buscar equipo o N°"
                )
                FiltroTipo(
                    tipo = filtro.tipo,
                    opciones = listOf(FiltroInventario.TODOS) + TIPOS_EQUIPO,
                    onElegir = viewModel::cambiarTipo,
                    cantidad = equipos.size
                )
            }
            if (equipos.isEmpty()) {
                item {
                    TextoAyuda(
                        if (filtro.busqueda.isNotBlank()) "Sin resultados" else "No hay equipos de este tipo",
                        centrado = true,
                        modifier = Modifier.padding(top = 24.dp)
                    )
                }
            }
            items(equipos, key = { it.id }) { equipo ->
                TarjetaEquipo(
                    equipo = equipo,
                    modoEncargado = modoEncargado,
                    onReservar = { onReservar(equipo.id) },
                    onReparado = {
                        viewModel.marcarReparado(equipo.id)
                        onMensaje("Equipo disponible de nuevo")
                    },
                    modifier = Modifier.animateItem()
                )
            }
        }
    }
}

@Composable
private fun TarjetaEquipo(
    equipo: EquipoEntity,
    modoEncargado: Boolean,
    onReservar: () -> Unit,
    onReparado: () -> Unit,
    modifier: Modifier = Modifier
) {
    // El docente puede reservar equipos Disponibles y también Prestados (para otro día)
    val reservable = !modoEncargado && equipo.estado != EstadoEquipo.MANTENCION
    val detalle = if (modoEncargado) {
        when (equipo.estado) {
            EstadoEquipo.MANTENCION -> "Requiere mantención"
            EstadoEquipo.PRESTADO -> "Está con un docente"
            else -> "Listo para reservar"
        }
    } else {
        when (equipo.estado) {
            EstadoEquipo.DISPONIBLE -> "Toca para reservar"
            EstadoEquipo.PRESTADO -> "Reservable otro día"
            else -> "No se puede reservar"
        }
    }
    val contenido: @Composable () -> Unit = {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                TileIcono(iconoTipo(equipo.tipo))
                Column(modifier = Modifier.weight(1f)) {
                    Text(equipo.nombre, style = MaterialTheme.typography.titleMedium)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        EstadoBadge(equipo.estado, tipoBadge(equipo.estado))
                        Text(
                            "#${equipo.id} · $detalle",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (reservable) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (modoEncargado && equipo.estado == EstadoEquipo.MANTENCION) {
                Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.End) {
                    FilledTonalButton(onClick = onReparado) {
                        Icon(Icons.Filled.Build, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Marcar como reparado")
                    }
                }
            }
        }
    }
    val tarjetaModifier = modifier
        .fillMaxWidth()
        .padding(bottom = 12.dp)
    if (reservable) {
        ElevatedCard(onClick = onReservar, modifier = tarjetaModifier) { contenido() }
    } else {
        ElevatedCard(modifier = tarjetaModifier) { contenido() }
    }
}
