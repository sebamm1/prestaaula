package com.example.prestaaula.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prestaaula.data.repository.PrestaAulaRepository
import com.example.prestaaula.model.EstadoReserva
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class EncargadoUi(
    val pendientes: List<ReservaItem> = emptyList(),   // reservas por aprobar
    val enUso: Int = 0,                                // equipos prestados ahora
    val historial: List<ReservaItem> = emptyList()     // préstamos (retirados y devueltos)
)

/** Menú del Encargado(a), Reservas por aprobar e Historial de préstamos. */
class EncargadoViewModel(private val repo: PrestaAulaRepository) : ViewModel() {

    val ui: StateFlow<EncargadoUi> = combine(repo.reservas, repo.equipos) { reservas, equipos ->
        EncargadoUi(
            pendientes = reservas.filter { it.estado == EstadoReserva.PENDIENTE }
                .sortedWith(ORDEN_PROXIMAS).conEquipos(equipos),
            enUso = reservas.count { it.estado == EstadoReserva.RETIRADA },
            historial = reservas.filter { it.estado == EstadoReserva.RETIRADA || it.estado == EstadoReserva.DEVUELTA }
                .sortedWith(ORDEN_RECIENTES).conEquipos(equipos)
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), EncargadoUi())

    /** Aceptar o rechazar (rechazar deja el bloque libre). */
    fun responder(reservaId: Int, aceptar: Boolean) {
        viewModelScope.launch { repo.responderReserva(reservaId, aceptar) }
    }
}
