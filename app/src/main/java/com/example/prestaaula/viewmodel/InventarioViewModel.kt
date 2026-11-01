package com.example.prestaaula.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prestaaula.data.local.entity.EquipoEntity
import com.example.prestaaula.data.repository.PrestaAulaRepository
import com.example.prestaaula.util.Texto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Filtros del inventario: buscador con lupa y desplegable por tipo (correcciones #1 y #3). */
data class FiltroInventario(val busqueda: String = "", val tipo: String = TODOS) {
    companion object { const val TODOS = "Todos" }
}

/** Inventario: lo usan el Docente (para reservar) y el Encargado(a) (para revisar y reparar). */
class InventarioViewModel(private val repo: PrestaAulaRepository) : ViewModel() {

    private val _filtro = MutableStateFlow(FiltroInventario())
    val filtro: StateFlow<FiltroInventario> = _filtro.asStateFlow()

    val equipos: StateFlow<List<EquipoEntity>> = combine(repo.equipos, _filtro) { lista, f ->
        val porTipo = lista.filter { f.tipo == FiltroInventario.TODOS || it.tipo == f.tipo }
        porTipo.filter { equipo ->
            Texto.coincide(equipo.nombre + " " + equipo.tipo + " " + equipo.marca + " #" + equipo.id, f.busqueda)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun cambiarBusqueda(texto: String) = _filtro.update { it.copy(busqueda = texto) }

    fun cambiarTipo(tipo: String) = _filtro.update { it.copy(tipo = tipo) }

    fun marcarReparado(equipoId: Int) {
        viewModelScope.launch { repo.marcarReparado(equipoId) }
    }
}
