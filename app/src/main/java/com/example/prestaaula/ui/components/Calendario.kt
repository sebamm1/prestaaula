@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.prestaaula.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.prestaaula.util.Fechas
import java.time.LocalDate
import java.time.YearMonth

private val DIAS_SEMANA = listOf("L", "M", "M", "J", "V", "S", "D")

/**
 * Calendario mensual hecho con Row y Column (grilla propia, igual a la maqueta).
 * - Días pasados y fines de semana: deshabilitados.
 * - Punto rojo: el equipo tiene horarios ocupados ese día.
 * - Al cambiar de mes, la grilla se desliza (animación).
 */
@Composable
fun Calendario(
    mes: YearMonth,
    onCambiarMes: (YearMonth) -> Unit,
    seleccionada: LocalDate?,
    deshabilitado: (LocalDate) -> Boolean,
    conOcupados: (LocalDate) -> Boolean,
    onElegir: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val hoy = Fechas.hoy()
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { onCambiarMes(mes.minusMonths(1)) },
                    enabled = mes > YearMonth.from(hoy)
                ) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Mes anterior")
                }
                Text(
                    Fechas.nombreMes(mes),
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { onCambiarMes(mes.plusMonths(1)) }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Mes siguiente")
                }
            }
            Row {
                DIAS_SEMANA.forEach { dia ->
                    Text(
                        dia,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 6.dp)
                    )
                }
            }
            AnimatedContent(
                targetState = mes,
                transitionSpec = {
                    val adelante = targetState > initialState
                    (slideInHorizontally { ancho -> if (adelante) ancho else -ancho } + fadeIn()) togetherWith
                        (slideOutHorizontally { ancho -> if (adelante) -ancho else ancho } + fadeOut())
                },
                label = "mesCalendario"
            ) { mesMostrado ->
                val desfase = mesMostrado.atDay(1).dayOfWeek.value - 1   // lunes = 0
                val diasDelMes = mesMostrado.lengthOfMonth()
                val filas = (desfase + diasDelMes + 6) / 7
                Column {
                    for (fila in 0 until filas) {
                        Row {
                            for (columna in 0 until 7) {
                                val numero = fila * 7 + columna - desfase + 1
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(46.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (numero in 1..diasDelMes) {
                                        val fecha = mesMostrado.atDay(numero)
                                        DiaCalendario(
                                            numero = numero,
                                            esHoy = fecha == hoy,
                                            seleccionado = fecha == seleccionada,
                                            habilitado = !deshabilitado(fecha),
                                            ocupado = conOcupados(fecha),
                                            onClick = { onElegir(fecha) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DiaCalendario(
    numero: Int,
    esHoy: Boolean,
    seleccionado: Boolean,
    habilitado: Boolean,
    ocupado: Boolean,
    onClick: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val fondo by animateColorAsState(if (seleccionado) cs.primary else Color.Transparent, label = "fondoDia")
    val colorTexto = when {
        seleccionado -> cs.onPrimary
        !habilitado -> cs.onSurface.copy(alpha = 0.38f)
        esHoy -> cs.primary
        else -> cs.onSurface
    }
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(fondo)
            .then(if (esHoy && !seleccionado) Modifier.border(1.dp, cs.primary, CircleShape) else Modifier)
            .clickable(enabled = habilitado, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text("$numero", style = MaterialTheme.typography.bodyMedium, color = colorTexto)
        if (ocupado) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 4.dp)
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(if (seleccionado) cs.onPrimary else cs.error)
            )
        }
    }
}
