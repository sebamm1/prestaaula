package com.example.prestaaula.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prestaaula.data.local.entity.EquipoEntity
import com.example.prestaaula.data.local.entity.ReservaEntity
import com.example.prestaaula.data.local.entity.UsuarioEntity
import com.example.prestaaula.data.repository.PrestaAulaRepository
import com.example.prestaaula.model.EstadoEquipo
import com.example.prestaaula.model.EstadoReserva
import com.example.prestaaula.util.Fechas
import com.example.prestaaula.validation.Validador
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

/** Lo que el docente va eligiendo en el formulario de reserva. */
data class ReservaForm(
    val curso: String = "",
    val fecha: LocalDate? = null,
    val bloque: Int? = null,
    val mes: YearMonth = YearMonth.now(),
    val tocados: Set<String> = emptySet(),
    val sacudir: Int = 0,
    val enviando: Boolean = false
)

/** El equipo que se reserva y sus reservas activas (para marcar choques en el calendario). */
data class ReservaDatos(
    val equipo: EquipoEntity? = null,
    val ocupadas: List<ReservaEntity> = emptyList()
)

/**
 * Nueva reserva. Recibe el id del equipo como argumento de navegación ("reserva/{equipoId}"),
 * igual que el detalle del Catálogo StingCommerce.
 */
class ReservaViewModel(
    savedStateHandle: SavedStateHandle,
    private val repo: PrestaAulaRepository
) : ViewModel() {

    val equipoId: Int = savedStateHandle.get<Int>("equipoId") ?: 0

    private val _form = MutableStateFlow(ReservaForm(mes = YearMonth.from(Fechas.primerDiaDisponible(false))))
    val form: StateFlow<ReservaForm> = _form.asStateFlow()

    val datos: StateFlow<ReservaDatos> = combine(repo.equipos, repo.reservas) { equipos, reservas ->
        ReservaDatos(
            equipo = equipos.firstOrNull { it.id == equipoId },
            ocupadas = reservas.filter { it.equipoId == equipoId && it.estado != EstadoReserva.RECHAZADA }
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, ReservaDatos())

    init {
        // El calendario abre en el mes del primer día disponible (si es fin de mes, abre el siguiente)
        viewModelScope.launch {
            val prestado = repo.obtenerEquipo(equipoId)?.estado == EstadoEquipo.PRESTADO
            _form.update { it.copy(mes = YearMonth.from(Fechas.primerDiaDisponible(prestado))) }
        }
    }

    // ---------- Consultas para la pantalla ----------
    private fun prestadoAhora(d: ReservaDatos) = d.equipo?.estado == EstadoEquipo.PRESTADO

    fun ocupadoPor(fecha: LocalDate?, bloque: Int?, d: ReservaDatos): String? {
        if (fecha == null || bloque == null) return null
        return d.ocupadas.firstOrNull { it.fecha == fecha.toString() && it.bloque == bloque }?.docenteNombre
    }

    fun diaDeshabilitado(fecha: LocalDate, d: ReservaDatos): Boolean =
        fecha.isBefore(Fechas.hoy()) || Fechas.esFinDeSemana(fecha) || (prestadoAhora(d) && fecha == Fechas.hoy())

    fun diaConOcupados(fecha: LocalDate, d: ReservaDatos): Boolean =
        d.ocupadas.any { it.fecha == fecha.toString() }

    /** Validación centralizada (Validador.reserva). */
    fun errores(f: ReservaForm, d: ReservaDatos): Map<String, String> =
        Validador.reserva(
            curso = f.curso,
            fecha = f.fecha,
            bloque = f.bloque,
            prestadoHoy = prestadoAhora(d) && f.fecha == Fechas.hoy(),
            ocupadoPor = ocupadoPor(f.fecha, f.bloque, d)
        )

    // ---------- Acciones ----------
    fun cambiarCurso(curso: String) = _form.update { it.copy(curso = curso, tocados = it.tocados + Validador.CURSO) }

    fun cambiarMes(mes: YearMonth) = _form.update { it.copy(mes = mes) }

    fun elegirFecha(fecha: LocalDate) =
        _form.update { it.copy(fecha = fecha, bloque = null, tocados = it.tocados + Validador.FECHA) }

    /** Devuelve true si el bloque está ocupado (la pantalla vibra en ese caso). */
    fun elegirBloque(bloque: Int): Boolean {
        _form.update { it.copy(bloque = bloque, tocados = it.tocados + Validador.BLOQUE) }
        return ocupadoPor(_form.value.fecha, bloque, datos.value) != null
    }

    fun reservar(usuario: UsuarioEntity, onExito: () -> Unit) {
        val f = _form.value.copy(tocados = setOf(Validador.CURSO, Validador.FECHA, Validador.BLOQUE))
        _form.value = f
        val fecha = f.fecha
        val bloque = f.bloque
        if (errores(f, datos.value).isNotEmpty() || fecha == null || bloque == null || f.enviando) {
            _form.update { it.copy(sacudir = it.sacudir + 1) }
            return
        }
        viewModelScope.launch {
            _form.update { it.copy(enviando = true) }
            val ok = repo.crearReserva(equipoId, usuario.id, usuario.nombre, f.curso, fecha.toString(), bloque)
            _form.update { it.copy(enviando = false, sacudir = if (ok) it.sacudir else it.sacudir + 1) }
            if (ok) onExito()
        }
    }
}
