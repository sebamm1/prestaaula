@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.prestaaula.ui.screens.encargado

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.prestaaula.model.EstadoDevolucion
import com.example.prestaaula.model.EstadoEquipo
import com.example.prestaaula.model.TIPOS_EQUIPO
import com.example.prestaaula.ui.components.BarraBusqueda
import com.example.prestaaula.ui.components.BotonPrincipal
import com.example.prestaaula.ui.components.CampoDesplegable
import com.example.prestaaula.ui.components.CampoTexto
import com.example.prestaaula.ui.components.EstadoBadge
import com.example.prestaaula.ui.components.FotoSelector
import com.example.prestaaula.ui.components.OpcionFila
import com.example.prestaaula.ui.components.PrestaTopBar
import com.example.prestaaula.ui.components.TextoApoyo
import com.example.prestaaula.ui.components.TextoAyuda
import com.example.prestaaula.ui.components.TipoBadge
import com.example.prestaaula.ui.components.TituloPaso
import com.example.prestaaula.util.Fechas
import com.example.prestaaula.validation.Validador
import com.example.prestaaula.viewmodel.AgregarEquipoViewModel
import com.example.prestaaula.viewmodel.AppViewModelProvider
import com.example.prestaaula.viewmodel.DevolucionViewModel

/** Chips de selección única (estado de la devolución y estado inicial del equipo). */
@Composable
private fun ChipsSeleccion(opciones: List<String>, seleccion: String, onElegir: (String) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        opciones.forEach { opcion ->
            val elegido = opcion == seleccion
            FilterChip(
                selected = elegido,
                onClick = { onElegir(opcion) },
                label = { Text(opcion) },
                leadingIcon = if (elegido) {
                    { Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(FilterChipDefaults.IconSize)) }
                } else null
            )
        }
    }
}

@Composable
private fun ErrorBajoCampo(error: String?, tocado: Boolean) {
    if (tocado && error != null) {
        Column(modifier = Modifier.padding(start = 16.dp, top = 2.dp)) {
            ProvideTextStyle(MaterialTheme.typography.bodySmall) { TextoApoyo(error, esError = true) }
        }
    }
}

/**
 * Registrar devolución (Encargado(a)):
 * 1) préstamo (buscador por docente, equipo o curso), 2) estado, 3) observaciones (opcionales), 4) foto del daño.
 */
@Composable
fun DevolucionScreen(
    onTema: () -> Unit,
    onSalir: () -> Unit,
    onVolver: () -> Unit,
    onRegistrada: () -> Unit,
    onMensaje: (String) -> Unit,
    viewModel: DevolucionViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val form by viewModel.form.collectAsState()
    val prestamos by viewModel.prestamos.collectAsState()
    val errores = viewModel.errores(form)

    Scaffold(
        topBar = { PrestaTopBar(titulo = "Registrar devolución", onTema = onTema, onVolver = onVolver, onSalir = onSalir) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            TituloPaso("1. ¿Qué préstamo devuelven?")
            BarraBusqueda(texto = form.busqueda, onCambio = viewModel::cambiarBusqueda, sugerencia = "Buscar por docente, equipo o curso")
            if (prestamos.lista.isEmpty()) {
                TextoAyuda(
                    if (prestamos.hayPrestados) "Sin resultados" else "No hay equipos prestados",
                    centrado = true,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            prestamos.lista.forEach { item ->
                val r = item.reserva
                OpcionFila(
                    titulo = item.nombreEquipo,
                    subtitulo = "${r.docenteNombre} · ${r.curso} · ${Fechas.texto(r.fecha)} · Bloque ${r.bloque} · retiró ${r.horaRetiro ?: "—"}",
                    seleccionada = form.prestamoId == r.id,
                    onClick = { viewModel.elegirPrestamo(r.id) },
                    adicional = if (r.horaDevDocente != null) {
                        {
                            EstadoBadge(
                                "El docente lo devolvió a las ${r.horaDevDocente}",
                                TipoBadge.ACENTO,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                        }
                    } else null
                )
            }
            ErrorBajoCampo(errores[Validador.PRESTAMO], Validador.PRESTAMO in form.tocados)

            TituloPaso("2. ¿En qué estado vuelve?")
            ChipsSeleccion(EstadoDevolucion.TODOS, form.estado, viewModel::elegirEstado)
            ErrorBajoCampo(errores[Validador.ESTADO], Validador.ESTADO in form.tocados)

            TituloPaso("3. Observaciones", nota = "opcional")
            CampoTexto(
                valor = form.observaciones,
                onCambio = viewModel::cambiarObservaciones,
                etiqueta = "Observaciones",
                ayuda = "Ej: pantalla trizada, falta cargador",
                lineas = 3,
                validar = false
            )

            TituloPaso("4. Foto del daño", nota = "opcional")
            FotoSelector(
                rutaFoto = form.fotoRuta,
                origen = form.fotoOrigen,
                onFoto = { ruta, origen ->
                    viewModel.ponerFoto(ruta, origen)
                    onMensaje(if (origen == "camara") "Foto tomada" else "Foto elegida de la galería")
                },
                onQuitar = viewModel::quitarFoto,
                onMensaje = onMensaje
            )

            BotonPrincipal(
                texto = "Registrar devolución",
                icono = Icons.AutoMirrored.Filled.Undo,
                onClick = { viewModel.registrar(onRegistrada) },
                sacudir = form.sacudir,
                habilitado = !form.enviando
            )
        }
    }
}

/** Agregar equipo (Encargado(a)): tipo (con "Otro"), marca, N° interno, estado inicial y foto opcional. */
@Composable
fun AgregarEquipoScreen(
    onTema: () -> Unit,
    onSalir: () -> Unit,
    onVolver: () -> Unit,
    onGuardado: () -> Unit,
    onMensaje: (String) -> Unit,
    viewModel: AgregarEquipoViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val form by viewModel.form.collectAsState()
    val ids by viewModel.idsEnUso.collectAsState()
    val errores = viewModel.errores(form, ids)

    Scaffold(
        topBar = { PrestaTopBar(titulo = "Agregar equipo", onTema = onTema, onVolver = onVolver, onSalir = onSalir) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            CampoDesplegable(
                valorMostrado = form.tipo,
                opciones = TIPOS_EQUIPO.map { it to it },
                onElegir = viewModel::cambiarTipo,
                etiqueta = "Tipo de equipo",
                icono = Icons.Filled.Category,
                error = errores[Validador.TIPO],
                tocado = Validador.TIPO in form.tocados
            )
            CampoTexto(
                valor = form.marca,
                onCambio = viewModel::cambiarMarca,
                etiqueta = "Marca",
                icono = Icons.Filled.Sell,
                ayuda = "Ej: Lenovo",
                error = errores[Validador.MARCA],
                tocado = Validador.MARCA in form.tocados,
                onSalir = { viewModel.marcarTocado(Validador.MARCA) }
            )
            CampoTexto(
                valor = form.codigo,
                onCambio = viewModel::cambiarCodigo,
                etiqueta = "N° de identificación interno",
                icono = Icons.Filled.Tag,
                ayuda = "Número entero, ej: 7",
                tipoTeclado = KeyboardType.Number,
                error = errores[Validador.CODIGO],
                tocado = Validador.CODIGO in form.tocados,
                onSalir = { viewModel.marcarTocado(Validador.CODIGO) }
            )

            TituloPaso("Estado inicial")
            ChipsSeleccion(listOf(EstadoEquipo.DISPONIBLE, EstadoEquipo.MANTENCION), form.estado, viewModel::cambiarEstado)
            ErrorBajoCampo(errores[Validador.ESTADO], Validador.ESTADO in form.tocados)

            TituloPaso("Foto del equipo", nota = "opcional")
            FotoSelector(
                rutaFoto = form.fotoRuta,
                origen = form.fotoOrigen,
                onFoto = { ruta, origen ->
                    viewModel.ponerFoto(ruta, origen)
                    onMensaje(if (origen == "camara") "Foto tomada" else "Foto elegida de la galería")
                },
                onQuitar = viewModel::quitarFoto,
                onMensaje = onMensaje
            )

            BotonPrincipal(
                texto = "Guardar equipo",
                icono = Icons.Filled.Check,
                onClick = { viewModel.guardar(onGuardado) },
                sacudir = form.sacudir,
                habilitado = !form.enviando
            )
        }
    }
}
