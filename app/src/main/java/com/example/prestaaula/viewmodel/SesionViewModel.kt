package com.example.prestaaula.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prestaaula.data.local.entity.UsuarioEntity
import com.example.prestaaula.data.local.entity.listaRoles
import com.example.prestaaula.data.repository.PrestaAulaRepository
import com.example.prestaaula.model.Rol
import com.example.prestaaula.util.Texto
import com.example.prestaaula.validation.Validador
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Estado del formulario de login. */
data class LoginUiState(
    val nombre: String = "",
    val correo: String = "",
    val rut: String = "",
    val tocados: Set<String> = emptySet(),          // campos que el usuario ya tocó (solo ahí se muestran errores)
    val errores: Map<String, String> = Validador.login("", "", ""),
    val errorGeneral: String? = null,
    val sacudir: Int = 0,                            // cambia para disparar la animación del botón
    val cargando: Boolean = false
)

/** Usuario con sesión iniciada y el rol con el que entró. */
data class SesionUiState(
    val usuario: UsuarioEntity? = null,
    val rol: Rol? = null
) {
    val roles: List<Rol> get() = usuario?.listaRoles() ?: emptyList()
}

/**
 * Login y sesión. Vive mientras la app está abierta (se crea en MainActivity),
 * así todas las pantallas saben quién entró y con qué rol.
 */
class SesionViewModel(private val repo: PrestaAulaRepository) : ViewModel() {

    private val _login = MutableStateFlow(LoginUiState())
    val login: StateFlow<LoginUiState> = _login.asStateFlow()

    private val _sesion = MutableStateFlow(SesionUiState())
    val sesion: StateFlow<SesionUiState> = _sesion.asStateFlow()

    init {
        // La primera vez que se abre la app, carga los datos de prueba en Room
        viewModelScope.launch { repo.asegurarDatosIniciales() }
    }

    private fun actualizar(cambio: (LoginUiState) -> LoginUiState) {
        _login.update { actual ->
            val nuevo = cambio(actual)
            nuevo.copy(errores = Validador.login(nuevo.nombre, nuevo.correo, nuevo.rut), errorGeneral = null)
        }
    }

    fun cambiarNombre(valor: String) = actualizar { it.copy(nombre = valor, tocados = it.tocados + Validador.NOMBRE) }
    fun cambiarCorreo(valor: String) = actualizar { it.copy(correo = valor, tocados = it.tocados + Validador.CORREO) }
    fun cambiarRut(valor: String) = actualizar { it.copy(rut = valor, tocados = it.tocados + Validador.RUT) }
    fun marcarTocado(campo: String) = actualizar { it.copy(tocados = it.tocados + campo) }

    /** Valida, busca al usuario en Room y abre la sesión. onListo recibe los roles del usuario. */
    fun ingresar(onListo: (List<Rol>) -> Unit) {
        val actual = _login.value.copy(tocados = setOf(Validador.NOMBRE, Validador.CORREO, Validador.RUT))
        _login.value = actual
        if (actual.errores.isNotEmpty() || actual.cargando) {
            _login.update { it.copy(sacudir = it.sacudir + 1) }
            return
        }
        viewModelScope.launch {
            _login.update { it.copy(cargando = true) }
            val usuario = repo.buscarUsuarioPorCorreo(actual.correo)
            if (usuario == null ||
                Validador.limpiarRut(usuario.rut) != Validador.limpiarRut(actual.rut) ||
                Texto.normalizar(usuario.nombre) != Texto.normalizar(actual.nombre)
            ) {
                _login.update {
                    it.copy(
                        cargando = false,
                        errorGeneral = "Los datos no coinciden con un usuario registrado",
                        sacudir = it.sacudir + 1
                    )
                }
                return@launch
            }
            val roles = usuario.listaRoles()
            _sesion.value = SesionUiState(usuario = usuario, rol = if (roles.size == 1) roles.first() else null)
            _login.value = LoginUiState()
            onListo(roles)
        }
    }

    fun elegirRol(rol: Rol) = _sesion.update { it.copy(rol = rol) }

    fun quitarRol() = _sesion.update { it.copy(rol = null) }

    fun salir() {
        _sesion.value = SesionUiState()
        _login.value = LoginUiState()
    }

    fun restablecerDatos(onListo: () -> Unit) {
        viewModelScope.launch {
            repo.restablecerDatos()
            salir()
            onListo()
        }
    }
}
