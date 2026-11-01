package com.example.prestaaula.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prestaaula.data.local.entity.ReservaEntity
import com.example.prestaaula.data.repository.PrestaAulaRepository
import com.example.prestaaula.model.EstadoReserva
import com.example.prestaaula.util.Fechas
import com.example.prestaaula.util.Texto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Un grupo de "Mis reservas" con su explicación (corrección v3: 3 grupos claros + Por aprobar). */
data class GrupoReservas(val titulo: String, val descripcion: String, val items: List<ReservaItem>)

data class MisReservasUi(
    val grupos: List<GrupoReservas> = emptyList(),
    val tieneReservas: Boolean = false
)

class MisReservasViewModel(private val repo: PrestaAulaRepository) : ViewModel() {

    private val docenteId = MutableStateFlow<Int?>(null)

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    val ui: StateFlow<MisReservasUi> =
        combine(repo.reservas, repo.equipos, docenteId, _busqueda) { reservas, equipos, id, q ->
            val mias = reservas.filter { it.docenteId == id }.conEquipos(equipos)
            val filtradas = mias.filter { Texto.coincide("${it.nombreEquipo} ${it.reserva.curso}", q) }
            fun grupo(estados: List<String>, titulo: String, descripcion: String) = GrupoReservas(
                titulo,
                descripcion,
                filtradas.filter { it.reserva.estado in estados }.sortedWith(compareBy<ReservaItem, ReservaEntity>(ORDEN_RECIENTES) { it.reserva })
            )
            MisReservasUi(
                grupos = listOf(
                    grupo(listOf(EstadoReserva.PENDIENTE), "Por aprobar",
                        "El Encargado(a) CRA todavía no acepta tu reserva. El botón se activa cuando la acepte."),
                    grupo(listOf(EstadoReserva.APROBADA), "Por retirar",
                        "El Encargado(a) aceptó tu reserva. Cuando retires el equipo del CRA, toca «Ya lo retiré»."),
                    grupo(listOf(EstadoReserva.RETIRADA), "En uso",
                        "Tienes el equipo contigo. Cuando lo devuelvas al CRA, toca «Ya lo devolví». El Encargado(a) registra en qué estado vuelve."),
                    grupo(listOf(EstadoReserva.DEVUELTA, EstadoReserva.RECHAZADA), "Historial",
                        "Equipos que ya devolviste y reservas rechazadas.")
                ).filter { it.items.isNotEmpty() },
                tieneReservas = mias.isNotEmpty()
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MisReservasUi())

    fun establecerDocente(id: Int) {
        docenteId.value = id
    }

    fun cambiarBusqueda(texto: String) {
        _busqueda.value = texto
    }

    /** "Ya lo retiré": solo funciona si el Encargado(a) ya aceptó la reserva (corrección #8). */
    fun retirar(reservaId: Int) {
        viewModelScope.launch { repo.confirmarRetiro(reservaId, Fechas.horaActual()) }
    }

    /** "Ya lo devolví": el docente confirma la devolución (caso, punto 3.1). */
    fun devolver(reservaId: Int) {
        viewModelScope.launch { repo.confirmarDevolucionDocente(reservaId, Fechas.horaActual()) }
    }
}
