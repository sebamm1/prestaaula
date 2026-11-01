package com.example.prestaaula.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prestaaula.data.repository.PrestaAulaRepository
import com.example.prestaaula.device.Fotos
import com.example.prestaaula.model.EstadoReserva
import com.example.prestaaula.util.Fechas
import com.example.prestaaula.util.Texto
import com.example.prestaaula.validation.Validador
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DevolucionForm(
    val busqueda: String = "",
    val prestamoId: Int? = null,
    val estado: String = "",
    val observaciones: String = "",
    val fotoRuta: String? = null,
    val fotoOrigen: String? = null,      // "camara" o "galeria"
    val tocados: Set<String> = emptySet(),
    val sacudir: Int = 0,
    val enviando: Boolean = false
)

data class PrestamosUi(
    val lista: List<ReservaItem> = emptyList(),
    val hayPrestados: Boolean = false
)

/** Registrar devolución (Encargado(a)): elegir préstamo, estado, observaciones opcionales y foto del daño. */
class DevolucionViewModel(private val repo: PrestaAulaRepository) : ViewModel() {

    private val _form = MutableStateFlow(DevolucionForm())
    val form: StateFlow<DevolucionForm> = _form.asStateFlow()

    /** Préstamos en uso. Los que el docente ya confirmó ("Ya lo devolví") aparecen primero. */
    val prestamos: StateFlow<PrestamosUi> = combine(repo.reservas, repo.equipos, _form) { reservas, equipos, f ->
        val enUso = reservas.filter { it.estado == EstadoReserva.RETIRADA }.conEquipos(equipos)
        PrestamosUi(
            lista = enUso
                .filter { Texto.coincide("${it.reserva.docenteNombre} ${it.nombreEquipo} ${it.reserva.curso}", f.busqueda) }
                .sortedByDescending { it.reserva.horaDevDocente != null },
            hayPrestados = enUso.isNotEmpty()
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PrestamosUi())

    fun errores(f: DevolucionForm): Map<String, String> = Validador.devolucion(f.prestamoId, f.estado)

    fun cambiarBusqueda(texto: String) = _form.update { it.copy(busqueda = texto) }
    fun elegirPrestamo(id: Int) = _form.update { it.copy(prestamoId = id, tocados = it.tocados + Validador.PRESTAMO) }
    fun elegirEstado(estado: String) = _form.update { it.copy(estado = estado, tocados = it.tocados + Validador.ESTADO) }
    fun cambiarObservaciones(texto: String) = _form.update { it.copy(observaciones = texto) }

    fun ponerFoto(ruta: String, origen: String) {
        val anterior = _form.value.fotoRuta
        if (anterior != null && anterior != ruta) Fotos.borrar(anterior)
        _form.update { it.copy(fotoRuta = ruta, fotoOrigen = origen) }
    }

    fun quitarFoto() {
        Fotos.borrar(_form.value.fotoRuta)
        _form.update { it.copy(fotoRuta = null, fotoOrigen = null) }
    }

    fun registrar(onExito: () -> Unit) {
        val f = _form.value.copy(tocados = setOf(Validador.PRESTAMO, Validador.ESTADO))
        _form.value = f
        val prestamoId = f.prestamoId
        if (errores(f).isNotEmpty() || prestamoId == null || f.enviando) {
            _form.update { it.copy(sacudir = it.sacudir + 1) }
            return
        }
        viewModelScope.launch {
            _form.update { it.copy(enviando = true) }
            repo.registrarDevolucion(prestamoId, f.estado, f.observaciones.trim(), f.fotoRuta, Fechas.horaActual())
            _form.value = DevolucionForm()
            onExito()
        }
    }
}
