package com.example.prestaaula.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prestaaula.data.local.entity.EquipoEntity
import com.example.prestaaula.data.repository.PrestaAulaRepository
import com.example.prestaaula.device.Fotos
import com.example.prestaaula.validation.Validador
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AgregarEquipoForm(
    val tipo: String = "",
    val marca: String = "",
    val codigo: String = "",
    val estado: String = "",
    val fotoRuta: String? = null,
    val fotoOrigen: String? = null,
    val tocados: Set<String> = emptySet(),
    val sacudir: Int = 0,
    val enviando: Boolean = false
)

/** Agregar equipo (Encargado(a)): tipo, marca, N° interno, estado inicial y foto opcional (corrección #2). */
class AgregarEquipoViewModel(private val repo: PrestaAulaRepository) : ViewModel() {

    private val _form = MutableStateFlow(AgregarEquipoForm())
    val form: StateFlow<AgregarEquipoForm> = _form.asStateFlow()

    /** N° internos ya usados (para avisar "Ese número ya está en uso" mientras se escribe). */
    val idsEnUso: StateFlow<Set<Int>> = repo.equipos
        .map { lista -> lista.map { it.id }.toSet() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptySet())

    fun errores(f: AgregarEquipoForm, ids: Set<Int>): Map<String, String> {
        val numero = f.codigo.trim().toIntOrNull()
        return Validador.equipo(f.tipo, f.marca, f.codigo, f.estado, codigoEnUso = numero != null && numero in ids)
    }

    fun cambiarTipo(v: String) = _form.update { it.copy(tipo = v, tocados = it.tocados + Validador.TIPO) }
    fun cambiarMarca(v: String) = _form.update { it.copy(marca = v, tocados = it.tocados + Validador.MARCA) }
    fun cambiarCodigo(v: String) = _form.update { it.copy(codigo = v, tocados = it.tocados + Validador.CODIGO) }
    fun cambiarEstado(v: String) = _form.update { it.copy(estado = v, tocados = it.tocados + Validador.ESTADO) }
    fun marcarTocado(campo: String) = _form.update { it.copy(tocados = it.tocados + campo) }

    fun ponerFoto(ruta: String, origen: String) {
        val anterior = _form.value.fotoRuta
        if (anterior != null && anterior != ruta) Fotos.borrar(anterior)
        _form.update { it.copy(fotoRuta = ruta, fotoOrigen = origen) }
    }

    fun quitarFoto() {
        Fotos.borrar(_form.value.fotoRuta)
        _form.update { it.copy(fotoRuta = null, fotoOrigen = null) }
    }

    fun guardar(onExito: () -> Unit) {
        val f = _form.value.copy(
            tocados = setOf(Validador.TIPO, Validador.MARCA, Validador.CODIGO, Validador.ESTADO)
        )
        _form.value = f
        val id = f.codigo.trim().toIntOrNull()
        if (errores(f, idsEnUso.value).isNotEmpty() || id == null || f.enviando) {
            _form.update { it.copy(sacudir = it.sacudir + 1) }
            return
        }
        viewModelScope.launch {
            _form.update { it.copy(enviando = true) }
            val marca = f.marca.trim()
            val equipo = EquipoEntity(
                id = id,
                tipo = f.tipo,
                marca = marca,
                nombre = "${f.tipo} $marca ${id.toString().padStart(2, '0')}",
                estado = f.estado,
                fotoRuta = f.fotoRuta
            )
            val ok = repo.agregarEquipo(equipo)
            if (ok) {
                _form.value = AgregarEquipoForm()
                onExito()
            } else {
                _form.update { it.copy(enviando = false, sacudir = it.sacudir + 1) }
            }
        }
    }
}
