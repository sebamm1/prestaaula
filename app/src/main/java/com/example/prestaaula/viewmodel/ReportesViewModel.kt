package com.example.prestaaula.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prestaaula.data.local.entity.EquipoEntity
import com.example.prestaaula.data.local.entity.ReservaEntity
import com.example.prestaaula.data.repository.PrestaAulaRepository
import com.example.prestaaula.model.EstadoDevolucion
import com.example.prestaaula.model.EstadoEquipo
import com.example.prestaaula.model.EstadoReserva
import com.example.prestaaula.model.TIPOS_EQUIPO
import com.example.prestaaula.util.Fechas
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class UsoTipo(val tipo: String, val cantidad: Int)
data class DocenteRanking(val nombre: String, val reservas: Int)
data class UltimoResponsable(val equipo: EquipoEntity, val reserva: ReservaEntity?)
data class IncidenciaEquipo(val equipo: EquipoEntity?, val equipoId: Int, val cantidad: Int, val ultima: ReservaEntity)

data class ReportesUi(
    val prestamosMes: Int = 0,
    val enMantencion: Int = 0,
    val incidenciasTotal: Int = 0,
    val usoPorTipo: List<UsoTipo> = emptyList(),
    val ranking: List<DocenteRanking> = emptyList(),
    val ultimos: List<UltimoResponsable> = emptyList(),
    val incidencias: List<IncidenciaEquipo> = emptyList()
)

/** Reportes (Encargado(a) y UTP / Administración). Todo se calcula con los datos de Room. */
class ReportesViewModel(private val repo: PrestaAulaRepository) : ViewModel() {

    val ui: StateFlow<ReportesUi> = combine(repo.equipos, repo.reservas) { equipos, reservas ->
        val mes = Fechas.hoy().toString().substring(0, 7)               // "yyyy-MM"
        val delMes = reservas.filter { it.fecha.startsWith(mes) }
        val esPrestamo = { r: ReservaEntity -> r.estado == EstadoReserva.RETIRADA || r.estado == EstadoReserva.DEVUELTA }
        val prestamosMes = delMes.filter(esPrestamo)
        val tipoDe = equipos.associate { it.id to it.tipo }

        // Incidencias: devoluciones "Con daño" o "Incompleto", agrupadas por equipo (más frecuentes primero)
        val incidencias = reservas
            .filter { it.estado == EstadoReserva.DEVUELTA && it.estadoDevolucion != null && it.estadoDevolucion != EstadoDevolucion.OK }
            .sortedWith(ORDEN_RECIENTES)
        val porEquipo = incidencias.groupBy { it.equipoId }.map { (id, lista) ->
            IncidenciaEquipo(
                equipo = equipos.firstOrNull { it.id == id },
                equipoId = id,
                cantidad = lista.size,
                ultima = lista.first()
            )
        }.sortedByDescending { it.cantidad }

        ReportesUi(
            prestamosMes = prestamosMes.size,
            enMantencion = equipos.count { it.estado == EstadoEquipo.MANTENCION },
            incidenciasTotal = incidencias.size,
            usoPorTipo = TIPOS_EQUIPO
                .map { tipo -> UsoTipo(tipo, prestamosMes.count { tipoDe[it.equipoId] == tipo }) }
                .filter { it.cantidad > 0 }
                .sortedByDescending { it.cantidad },
            ranking = delMes.groupingBy { it.docenteNombre }.eachCount()
                .map { (nombre, cantidad) -> DocenteRanking(nombre, cantidad) }
                .sortedByDescending { it.reservas },
            ultimos = equipos.map { equipo ->
                UltimoResponsable(
                    equipo = equipo,
                    reserva = reservas.filter { it.equipoId == equipo.id && esPrestamo(it) }.sortedWith(ORDEN_RECIENTES).firstOrNull()
                )
            },
            incidencias = porEquipo
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ReportesUi())
}
