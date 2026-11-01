@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.prestaaula.ui.screens.encargado

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.prestaaula.data.local.entity.UsuarioEntity
import com.example.prestaaula.model.EstadoDevolucion
import com.example.prestaaula.model.EstadoReserva
import com.example.prestaaula.model.horarioDe
import com.example.prestaaula.ui.components.EstadoBadge
import com.example.prestaaula.ui.components.PrestaTopBar
import com.example.prestaaula.ui.components.TarjetaMenu
import com.example.prestaaula.ui.components.TextoAyuda
import com.example.prestaaula.ui.components.TileIcono
import com.example.prestaaula.ui.components.TipoBadge
import com.example.prestaaula.ui.components.iconoTipo
import com.example.prestaaula.util.Fechas
import com.example.prestaaula.viewmodel.AppViewModelProvider
import com.example.prestaaula.viewmodel.EncargadoViewModel

/** Destinos del menú del Encargado(a). */
enum class OpcionEncargado { APROBAR, DEVOLUCION, HISTORIAL, INVENTARIO, AGREGAR, REPORTES }

/** Menú principal del Encargado(a) CRA. */
@Composable
fun EncargadoMenuScreen(
    usuario: UsuarioEntity,
    onTema: () -> Unit,
    onSalir: () -> Unit,
    onIr: (OpcionEncargado) -> Unit,
    viewModel: EncargadoViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val ui by viewModel.ui.collectAsState()
    val p = ui.pendientes.size
    Scaffold(
        topBar = { PrestaTopBar(titulo = "Encargado(a) CRA", onTema = onTema, onSalir = onSalir) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            TextoAyuda(
                "Hola, ${usuario.nombre.substringBefore(" ")}. Hay $p ${if (p == 1) "reserva" else "reservas"} por aprobar " +
                    "y ${ui.enUso} ${if (ui.enUso == 1) "equipo" else "equipos"} en uso.",
                modifier = Modifier.padding(bottom = 12.dp)
            )
            TarjetaMenu("Reservas por aprobar", "Aceptar o rechazar las reservas de los docentes",
                Icons.Filled.HourglassEmpty, onClick = { onIr(OpcionEncargado.APROBAR) }, contador = p)
            TarjetaMenu("Registrar devolución", "Marcar el estado del equipo y anotar observaciones",
                Icons.AutoMirrored.Filled.Undo, onClick = { onIr(OpcionEncargado.DEVOLUCION) })
            TarjetaMenu("Historial de préstamos", "Quién retiró cada equipo, a qué hora y cómo lo devolvió",
                Icons.Filled.History, onClick = { onIr(OpcionEncargado.HISTORIAL) })
            TarjetaMenu("Ver inventario", "Qué equipos están libres, prestados o en mantención",
                Icons.Filled.Inventory2, onClick = { onIr(OpcionEncargado.INVENTARIO) })
            TarjetaMenu("Agregar equipo", "Registrar un equipo nuevo en el inventario",
                Icons.Filled.Add, onClick = { onIr(OpcionEncargado.AGREGAR) })
            TarjetaMenu("Ver reportes", "Uso de equipos, incidencias y último responsable",
                Icons.Filled.BarChart, onClick = { onIr(OpcionEncargado.REPORTES) })
        }
    }
}

/** Reservas por aprobar: Aceptar o Rechazar (corrección #8). Rechazar deja el bloque libre. */
@Composable
fun AprobarScreen(
    onTema: () -> Unit,
    onSalir: () -> Unit,
    onVolver: () -> Unit,
    onMensaje: (String) -> Unit,
    viewModel: EncargadoViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val ui by viewModel.ui.collectAsState()
    Scaffold(
        topBar = { PrestaTopBar(titulo = "Reservas por aprobar", onTema = onTema, onVolver = onVolver, onSalir = onSalir) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp)
        ) {
            if (ui.pendientes.isEmpty()) {
                item { TextoAyuda("No hay reservas por aprobar", centrado = true, modifier = Modifier.padding(top = 24.dp)) }
            } else {
                item {
                    TextoAyuda(
                        "Si rechazas una reserva, el bloque queda libre para otro docente.",
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
            }
            items(ui.pendientes, key = { it.reserva.id }) { item ->
                val r = item.reserva
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .animateItem()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            TileIcono(iconoTipo(item.equipo?.tipo ?: ""))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.nombreEquipo, style = MaterialTheme.typography.titleMedium)
                                Text("${r.docenteNombre} · ${r.curso}", style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${Fechas.texto(r.fecha)} · Bloque ${r.bloque} (${horarioDe(r.bloque)})",
                                    style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    viewModel.responder(r.id, aceptar = false)
                                    onMensaje("Reserva rechazada: el bloque queda libre")
                                },
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Filled.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Rechazar")
                            }
                            Button(
                                onClick = {
                                    viewModel.responder(r.id, aceptar = true)
                                    onMensaje("Reserva aceptada")
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Aceptar")
                            }
                        }
                    }
                }
            }
        }
    }
}

/** Historial de préstamos (correcciones #9 y #10): quién retiró, horas, estado y observaciones. */
@Composable
fun HistorialScreen(
    onTema: () -> Unit,
    onSalir: () -> Unit,
    onVolver: () -> Unit,
    viewModel: EncargadoViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val ui by viewModel.ui.collectAsState()
    Scaffold(
        topBar = { PrestaTopBar(titulo = "Historial de préstamos", onTema = onTema, onVolver = onVolver, onSalir = onSalir) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                TextoAyuda("Todos los préstamos, del más reciente al más antiguo.", modifier = Modifier.padding(bottom = 12.dp))
            }
            if (ui.historial.isEmpty()) {
                item { TextoAyuda("Aún no hay préstamos", centrado = true, modifier = Modifier.padding(top = 24.dp)) }
            }
            items(ui.historial, key = { it.reserva.id }) { item ->
                val r = item.reserva
                val devuelto = r.estado == EstadoReserva.DEVUELTA
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            TileIcono(iconoTipo(item.equipo?.tipo ?: ""))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.nombreEquipo, style = MaterialTheme.typography.titleMedium)
                                Text("${r.docenteNombre} · ${r.curso}", style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            when {
                                !devuelto -> EstadoBadge("En uso", TipoBadge.PRESTADO)
                                r.estadoDevolucion == EstadoDevolucion.OK -> EstadoBadge("Devuelto OK", TipoBadge.DISPONIBLE)
                                else -> EstadoBadge(r.estadoDevolucion ?: "Devuelto", TipoBadge.MANTENCION)
                            }
                        }
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("${Fechas.texto(r.fecha)} · Bloque ${r.bloque} (${horarioDe(r.bloque)})",
                                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Retiró ${r.horaRetiro ?: "—"} · Devolvió ${r.horaDevolucion ?: "pendiente"}",
                                    style = MaterialTheme.typography.titleSmall)
                                if (r.horaDevDocente != null) {
                                    Text("Docente confirmó la devolución a las ${r.horaDevDocente}",
                                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                if (devuelto && !r.observaciones.isNullOrBlank()) {
                                    Text("Obs.: ${r.observaciones}", style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                if (r.fotoDanoRuta != null) {
                                    Text("Con foto del daño", style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
