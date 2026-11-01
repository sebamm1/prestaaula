@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.prestaaula.ui.screens.docente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.prestaaula.data.local.entity.UsuarioEntity
import com.example.prestaaula.model.BLOQUES
import com.example.prestaaula.model.CURSOS
import com.example.prestaaula.model.EstadoEquipo
import com.example.prestaaula.ui.components.BotonPrincipal
import com.example.prestaaula.ui.components.Calendario
import com.example.prestaaula.ui.components.CampoDesplegable
import com.example.prestaaula.ui.components.DocenteBottomBar
import com.example.prestaaula.ui.components.OpcionFila
import com.example.prestaaula.ui.components.PestanaDocente
import com.example.prestaaula.ui.components.PrestaTopBar
import com.example.prestaaula.ui.components.TextoApoyo
import com.example.prestaaula.ui.components.TextoAyuda
import com.example.prestaaula.ui.components.TileIcono
import com.example.prestaaula.ui.components.TituloPaso
import com.example.prestaaula.ui.components.iconoTipo
import com.example.prestaaula.validation.Validador
import com.example.prestaaula.viewmodel.AppViewModelProvider
import com.example.prestaaula.viewmodel.ReservaViewModel

/**
 * Nueva reserva: 1) curso (desplegable 1° a 8° básico), 2) día en el calendario, 3) bloque libre.
 * Avisa si el bloque está ocupado (y vibra).
 */
@Composable
fun ReservaScreen(
    usuario: UsuarioEntity,
    onTema: () -> Unit,
    onSalir: () -> Unit,
    onVolver: () -> Unit,
    onInventario: () -> Unit,
    onMisReservas: () -> Unit,
    onReservado: () -> Unit,
    onVibrar: () -> Unit,
    viewModel: ReservaViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val form by viewModel.form.collectAsState()
    val datos by viewModel.datos.collectAsState()
    val errores = viewModel.errores(form, datos)
    val equipo = datos.equipo

    Scaffold(
        topBar = { PrestaTopBar(titulo = "Nueva reserva", onTema = onTema, onVolver = onVolver, onSalir = onSalir) },
        bottomBar = { DocenteBottomBar(PestanaDocente.INVENTARIO, onInventario = onInventario, onMisReservas = onMisReservas) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Equipo que se reserva
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    TileIcono(if (equipo != null) iconoTipo(equipo.tipo) else Icons.Filled.Inventory2)
                    Column {
                        Text("Vas a reservar", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(equipo?.nombre ?: "Cargando...", style = MaterialTheme.typography.titleMedium)
                        if (equipo?.estado == EstadoEquipo.PRESTADO) {
                            Text(
                                "Está prestado ahora: puedes reservarlo para otro día.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // 1. Curso
            TituloPaso("1. ¿Para qué curso?")
            CampoDesplegable(
                valorMostrado = CURSOS.firstOrNull { it.valor == form.curso }?.etiqueta ?: "",
                opciones = CURSOS.map { it.valor to it.etiqueta },
                onElegir = viewModel::cambiarCurso,
                etiqueta = "Curso",
                icono = Icons.Filled.School,
                error = errores[Validador.CURSO],
                tocado = Validador.CURSO in form.tocados
            )

            // 2. Día
            TituloPaso("2. Elige el día", nota = "punto rojo: hay horarios ocupados")
            Calendario(
                mes = form.mes,
                onCambiarMes = viewModel::cambiarMes,
                seleccionada = form.fecha,
                deshabilitado = { fecha -> viewModel.diaDeshabilitado(fecha, datos) },
                conOcupados = { fecha -> viewModel.diaConOcupados(fecha, datos) },
                onElegir = viewModel::elegirFecha
            )
            ErrorDeCampo(errores[Validador.FECHA], Validador.FECHA in form.tocados)

            // 3. Bloque
            TituloPaso("3. Elige un horario libre")
            if (form.fecha == null) {
                TextoAyuda("Elige primero un día en el calendario", centrado = true, modifier = Modifier.padding(vertical = 8.dp))
            } else {
                BLOQUES.forEach { bloque ->
                    val ocupadoPor = viewModel.ocupadoPor(form.fecha, bloque.numero, datos)
                    val elegido = form.bloque == bloque.numero
                    OpcionFila(
                        titulo = "Bloque ${bloque.numero}",
                        subtitulo = bloque.horario,
                        seleccionada = elegido,
                        ocupada = ocupadoPor != null,
                        textoDerecha = when {
                            ocupadoPor != null -> "Ocupado por\n$ocupadoPor"
                            elegido -> "Elegido"
                            else -> "Libre"
                        },
                        onClick = {
                            val ocupado = viewModel.elegirBloque(bloque.numero)
                            if (ocupado) onVibrar()   // aviso de choque: vibra
                        }
                    )
                }
            }
            ErrorDeCampo(errores[Validador.BLOQUE], Validador.BLOQUE in form.tocados)

            BotonPrincipal(
                texto = if (equipo != null && form.bloque != null) "Reservar ${equipo.nombre} · Bloque ${form.bloque}" else "Reservar",
                onClick = { viewModel.reservar(usuario, onReservado) },
                sacudir = form.sacudir,
                habilitado = !form.enviando
            )
            TextoAyuda(
                "Tu reserva queda por aprobar hasta que el Encargado(a) CRA la acepte.",
                centrado = true,
                modifier = Modifier.padding(top = 10.dp)
            )
        }
    }
}

/** Mensaje de error bajo el calendario o los bloques (solo si el campo ya fue tocado). */
@Composable
private fun ErrorDeCampo(error: String?, tocado: Boolean) {
    if (tocado && error != null) {
        Column(modifier = Modifier.padding(start = 16.dp, top = 4.dp)) {
            ProvideTextStyle(MaterialTheme.typography.bodySmall) { TextoApoyo(error, esError = true) }
        }
    }
}
