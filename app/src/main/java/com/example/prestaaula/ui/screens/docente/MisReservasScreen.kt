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
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.prestaaula.data.local.entity.UsuarioEntity
import com.example.prestaaula.model.EstadoReserva
import com.example.prestaaula.model.horarioDe
import com.example.prestaaula.ui.components.BarraBusqueda
import com.example.prestaaula.ui.components.BotonPrincipal
import com.example.prestaaula.ui.components.DocenteBottomBar
import com.example.prestaaula.ui.components.EstadoBadge
import com.example.prestaaula.ui.components.PestanaDocente
import com.example.prestaaula.ui.components.PrestaTopBar
import com.example.prestaaula.ui.components.TextoAyuda
import com.example.prestaaula.ui.components.TileIcono
import com.example.prestaaula.ui.components.TipoBadge
import com.example.prestaaula.ui.components.iconoTipo
import com.example.prestaaula.util.Fechas
import com.example.prestaaula.viewmodel.AppViewModelProvider
import com.example.prestaaula.viewmodel.MisReservasViewModel
import com.example.prestaaula.viewmodel.ReservaItem

/**
 * Mis reservas (Docente), en grupos: Por aprobar → Por retirar → En uso → Historial.
 * "Ya lo retiré" se activa cuando el Encargado(a) acepta y se bloquea al retirar (corrección #8).
 * "Ya lo devolví" confirma la devolución (caso, punto 3.1).
 */
@Composable
fun MisReservasScreen(
    usuario: UsuarioEntity,
    onTema: () -> Unit,
    onSalir: () -> Unit,
    onVolver: () -> Unit,
    onInventario: () -> Unit,
    onMensaje: (String) -> Unit,
    viewModel: MisReservasViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    LaunchedEffect(usuario.id) { viewModel.establecerDocente(usuario.id) }
    val ui by viewModel.ui.collectAsState()
    val busqueda by viewModel.busqueda.collectAsState()

    Scaffold(
        topBar = { PrestaTopBar(titulo = "Mis reservas", onTema = onTema, onVolver = onVolver, onSalir = onSalir) },
        bottomBar = { DocenteBottomBar(PestanaDocente.MIS_RESERVAS, onInventario = onInventario, onMisReservas = {}) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                BarraBusqueda(texto = busqueda, onCambio = viewModel::cambiarBusqueda, sugerencia = "Buscar por equipo o curso")
            }
            if (ui.grupos.isEmpty()) {
                item {
                    TextoAyuda(
                        if (ui.tieneReservas) "Sin resultados" else "Aún no tienes reservas",
                        centrado = true,
                        modifier = Modifier.padding(top = 24.dp)
                    )
                    if (!ui.tieneReservas) {
                        BotonPrincipal(texto = "Reservar un equipo", onClick = onInventario)
                    }
                }
            }
            ui.grupos.forEach { grupo ->
                item(key = "titulo_${grupo.titulo}") {
                    Column(modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)) {
                        Text(
                            "${grupo.titulo} (${grupo.items.size})",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        TextoAyuda(grupo.descripcion, modifier = Modifier.padding(top = 2.dp))
                    }
                }
                items(grupo.items, key = { it.reserva.id }) { item ->
                    TarjetaReserva(
                        item = item,
                        onRetirar = {
                            viewModel.retirar(item.reserva.id)
                            onMensaje("Retiro confirmado")
                        },
                        onDevolver = {
                            viewModel.devolver(item.reserva.id)
                            onMensaje("Devolución confirmada: el Encargado(a) registrará cómo vuelve")
                        },
                        modifier = Modifier.animateItem()
                    )
                }
            }
        }
    }
}

@Composable
private fun TarjetaReserva(
    item: ReservaItem,
    onRetirar: () -> Unit,
    onDevolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    val r = item.reserva
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                TileIcono(iconoTipo(item.equipo?.tipo ?: ""))
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.nombreEquipo, style = MaterialTheme.typography.titleMedium)
                    Text(
                        "${r.curso} · ${Fechas.texto(r.fecha)} · Bloque ${r.bloque} (${horarioDe(r.bloque)})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                when (r.estado) {
                    EstadoReserva.DEVUELTA -> EstadoBadge("Devuelta", TipoBadge.DISPONIBLE)
                    EstadoReserva.RECHAZADA -> EstadoBadge("Rechazada", TipoBadge.NEUTRO)
                }
            }
            when (r.estado) {
                // Esperando al Encargado(a): botón desactivado
                EstadoReserva.PENDIENTE -> BotonAccion("Ya lo retiré", Icons.Filled.HourglassEmpty, habilitado = false, onClick = {})
                // Aceptada: se puede confirmar el retiro
                EstadoReserva.APROBADA -> BotonAccion("Ya lo retiré", Icons.Filled.Check, habilitado = true, onClick = onRetirar)
                // En uso: el retiro queda bloqueado; se puede confirmar la devolución
                EstadoReserva.RETIRADA -> {
                    if (r.horaDevDocente != null) {
                        BotonAccion(
                            "Devuelto ${r.horaDevDocente} · falta recepción",
                            Icons.Filled.HourglassEmpty,
                            habilitado = false,
                            onClick = {}
                        )
                    } else {
                        TextoAyuda("Retirado a las ${r.horaRetiro ?: "—"}", modifier = Modifier.padding(top = 10.dp))
                        BotonAccion("Ya lo devolví", Icons.AutoMirrored.Filled.Undo, habilitado = true, onClick = onDevolver)
                    }
                }
                EstadoReserva.DEVUELTA -> {
                    TextoAyuda(
                        "Retiró ${r.horaRetiro ?: "—"} · Devolvió ${r.horaDevolucion ?: "—"}",
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun BotonAccion(texto: String, icono: ImageVector, habilitado: Boolean, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.End) {
        Button(onClick = onClick, enabled = habilitado) {
            Icon(
                if (!habilitado && icono == Icons.Filled.Check) Icons.Filled.CheckCircle else icono,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(texto)
        }
    }
}
