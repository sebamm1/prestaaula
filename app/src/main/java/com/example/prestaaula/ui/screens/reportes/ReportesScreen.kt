@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.prestaaula.ui.screens.reportes

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.prestaaula.ui.components.EstadoBadge
import com.example.prestaaula.ui.components.PrestaTopBar
import com.example.prestaaula.ui.components.TextoAyuda
import com.example.prestaaula.ui.components.TipoBadge
import com.example.prestaaula.ui.components.TituloPaso
import com.example.prestaaula.util.Fechas
import com.example.prestaaula.viewmodel.AppViewModelProvider
import com.example.prestaaula.viewmodel.ReportesViewModel

/**
 * Reportes (UTP / Administración y Encargado(a)):
 * totales del mes, uso por tipo, ranking de docentes (corrección #6),
 * último responsable de cada equipo e incidencias agrupadas por equipo.
 */
@Composable
fun ReportesScreen(
    onTema: () -> Unit,
    onSalir: () -> Unit,
    onVolver: (() -> Unit)?,
    viewModel: ReportesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val ui by viewModel.ui.collectAsState()

    Scaffold(
        topBar = { PrestaTopBar(titulo = "Reportes", onTema = onTema, onVolver = onVolver, onSalir = onSalir) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Totales
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Kpi(ui.prestamosMes, "Préstamos del mes", Modifier.weight(1f))
                Kpi(ui.enMantencion, "En mantención", Modifier.weight(1f))
                Kpi(ui.incidenciasTotal, "Incidencias", Modifier.weight(1f))
            }

            // Uso por tipo de equipo
            TituloPaso("Uso por tipo de equipo", nota = "préstamos del mes")
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (ui.usoPorTipo.isEmpty()) {
                        TextoAyuda("Sin préstamos este mes", centrado = true)
                    } else {
                        val maximo = ui.usoPorTipo.first().cantidad.coerceAtLeast(1)
                        ui.usoPorTipo.forEach { uso ->
                            BarraUso(uso.tipo, uso.cantidad, uso.cantidad.toFloat() / maximo)
                        }
                    }
                }
            }

            // Ranking de docentes
            TituloPaso("Docentes con más reservas del mes")
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                if (ui.ranking.isEmpty()) {
                    TextoAyuda("Sin reservas este mes", centrado = true, modifier = Modifier.padding(16.dp))
                }
                ui.ranking.forEachIndexed { i, docente ->
                    if (i > 0) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("${i + 1}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(docente.nombre, style = MaterialTheme.typography.titleSmall)
                            Text(
                                "${docente.reservas} ${if (docente.reservas == 1) "reserva pedida" else "reservas pedidas"} este mes",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (i == 0) EstadoBadge("Más reservas", TipoBadge.ACENTO)
                    }
                }
            }

            // Último responsable de cada equipo
            TituloPaso("Último responsable de cada equipo")
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                ui.ultimos.forEachIndexed { i, ultimo ->
                    if (i > 0) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    val r = ultimo.reserva
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                        Text(ultimo.equipo.nombre, style = MaterialTheme.typography.titleSmall)
                        Text(
                            if (r == null) "Sin préstamos registrados"
                            else "${r.docenteNombre} · ${r.curso} · ${Fechas.texto(r.fecha)} · retiró ${r.horaRetiro ?: "—"} · devolvió ${r.horaDevolucion ?: "pendiente"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Incidencias agrupadas por equipo
            TituloPaso("Equipos con incidencias", nota = "más frecuentes primero")
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                if (ui.incidencias.isEmpty()) {
                    TextoAyuda("Sin incidencias", centrado = true, modifier = Modifier.padding(16.dp))
                }
                ui.incidencias.forEachIndexed { i, inc ->
                    if (i > 0) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    val u = inc.ultima
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Filled.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(inc.equipo?.nombre ?: "Equipo #${inc.equipoId}", style = MaterialTheme.typography.titleSmall)
                            Text(
                                "Última: ${u.docenteNombre} · ${Fechas.texto(u.fecha)} · " +
                                    (u.observaciones?.takeIf { it.isNotBlank() } ?: u.estadoDevolucion ?: ""),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        EstadoBadge(
                            "${inc.cantidad} ${if (inc.cantidad == 1) "incidencia" else "incidencias"}",
                            if (inc.cantidad > 1) TipoBadge.MANTENCION else TipoBadge.NEUTRO
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Kpi(valor: Int, etiqueta: String, modifier: Modifier = Modifier) {
    ElevatedCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("$valor", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
            Text(
                etiqueta,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

/** Barra horizontal que "crece" al entrar a la pantalla (animación). */
@Composable
private fun BarraUso(tipo: String, cantidad: Int, fraccion: Float) {
    var iniciado by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { iniciado = true }
    val ancho by animateFloatAsState(
        targetValue = if (iniciado) fraccion else 0f,
        animationSpec = tween(durationMillis = 700),
        label = "barraUso"
    )
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(tipo, style = MaterialTheme.typography.bodySmall, modifier = Modifier.width(78.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(ancho)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
        Text("$cantidad", style = MaterialTheme.typography.labelLarge, modifier = Modifier.width(22.dp), textAlign = TextAlign.End)
    }
}
