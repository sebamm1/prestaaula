@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.prestaaula.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.prestaaula.data.local.entity.UsuarioEntity
import com.example.prestaaula.device.Vibrador
import com.example.prestaaula.model.Rol
import com.example.prestaaula.ui.components.AvisoVibracion
import com.example.prestaaula.ui.screens.docente.InventarioScreen
import com.example.prestaaula.ui.screens.docente.MisReservasScreen
import com.example.prestaaula.ui.screens.docente.ReservaScreen
import com.example.prestaaula.ui.screens.encargado.AgregarEquipoScreen
import com.example.prestaaula.ui.screens.encargado.AprobarScreen
import com.example.prestaaula.ui.screens.encargado.DevolucionScreen
import com.example.prestaaula.ui.screens.encargado.EncargadoMenuScreen
import com.example.prestaaula.ui.screens.encargado.HistorialScreen
import com.example.prestaaula.ui.screens.encargado.OpcionEncargado
import com.example.prestaaula.ui.screens.login.ElegirRolScreen
import com.example.prestaaula.ui.screens.login.LoginScreen
import com.example.prestaaula.ui.screens.reportes.ReportesScreen
import com.example.prestaaula.viewmodel.SesionViewModel
import kotlinx.coroutines.launch

/** Rutas de la app (cada pantalla es una ruta del NavHost, Semana 6). */
object Rutas {
    const val LOGIN = "login"
    const val ELEGIR_ROL = "elegir_rol"
    const val INVENTARIO = "inventario"
    const val RESERVA = "reserva/{equipoId}"
    fun reservaDe(equipoId: Int) = "reserva/$equipoId"
    const val MIS_RESERVAS = "mis_reservas"
    const val ENCARGADO = "encargado"
    const val APROBAR = "aprobar"
    const val DEVOLUCION = "devolucion"
    const val HISTORIAL = "historial"
    const val INVENTARIO_ENCARGADO = "inventario_encargado"
    const val AGREGAR_EQUIPO = "agregar_equipo"
    const val REPORTES = "reportes"

    /** Pantalla de inicio de cada rol. */
    fun inicioDe(rol: Rol): String = when (rol) {
        Rol.DOCENTE -> INVENTARIO
        Rol.ENCARGADO -> ENCARGADO
        Rol.UTP -> REPORTES
    }
}

/**
 * Navegación de PrestaAula.
 * - Animación al cambiar de pantalla (deslizar + desvanecer), como en "Navegación avanzada".
 * - Mensajes (Snackbar) y aviso de vibración compartidos por todas las pantallas.
 */
@Composable
fun AppNavigation(
    sesionViewModel: SesionViewModel,
    onTema: () -> Unit
) {
    val navController = rememberNavController()
    val sesion by sesionViewModel.sesion.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var vibraciones by remember { mutableIntStateOf(0) }

    val mostrarMensaje: (String) -> Unit = { mensaje ->
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(mensaje)
        }
    }
    val vibrar: () -> Unit = {
        Vibrador.vibrar(context)   // recurso del dispositivo
        vibraciones++              // aviso visual (el emulador no vibra)
    }

    val rutaActual = navController.currentBackStackEntryAsState().value?.destination?.route
    val conBarraInferior = rutaActual == Rutas.INVENTARIO || rutaActual == Rutas.RESERVA || rutaActual == Rutas.MIS_RESERVAS
    val variosRoles = sesion.roles.size > 1

    val salir: () -> Unit = {
        sesionViewModel.salir()
        navController.navigate(Rutas.LOGIN) { popUpTo(navController.graph.id) { inclusive = true } }
    }
    val irAInventarioDocente: () -> Unit = {
        if (!navController.popBackStack(Rutas.INVENTARIO, inclusive = false)) {
            navController.navigate(Rutas.INVENTARIO)
        }
    }
    val irAMisReservas: () -> Unit = {
        navController.navigate(Rutas.MIS_RESERVAS) {
            popUpTo(Rutas.INVENTARIO)
            launchSingleTop = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Rutas.LOGIN,
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(300)) + fadeIn(tween(300))
            },
            exitTransition = { fadeOut(tween(200)) },
            popEnterTransition = { fadeIn(tween(300)) },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(300)) + fadeOut(tween(300))
            }
        ) {
            // ---------- Login y elección de rol ----------
            composable(Rutas.LOGIN) {
                LoginScreen(
                    sesionViewModel = sesionViewModel,
                    onTema = onTema,
                    onIngresado = { roles ->
                        val nombre = sesionViewModel.sesion.value.usuario?.nombre ?: ""
                        mostrarMensaje("Bienvenido(a), $nombre")
                        val destino = if (roles.size == 1) Rutas.inicioDe(roles.first()) else Rutas.ELEGIR_ROL
                        navController.navigate(destino) { popUpTo(Rutas.LOGIN) { inclusive = true } }
                    },
                    onRestablecer = {
                        sesionViewModel.restablecerDatos { mostrarMensaje("Datos restablecidos") }
                    }
                )
            }
            composable(Rutas.ELEGIR_ROL) {
                // Al volver a esta pantalla, el color vuelve al de base (sin rol)
                LaunchedEffect(Unit) { sesionViewModel.quitarRol() }
                RequiereSesion(sesion.usuario, navController) { usuario ->
                    ElegirRolScreen(
                        usuario = usuario,
                        onTema = onTema,
                        onSalir = salir,
                        onElegir = { rol ->
                            sesionViewModel.elegirRol(rol)
                            navController.navigate(Rutas.inicioDe(rol))
                        }
                    )
                }
            }

            // ---------- Docente ----------
            composable(Rutas.INVENTARIO) {
                RequiereSesion(sesion.usuario, navController) {
                    InventarioScreen(
                        modoEncargado = false,
                        onTema = onTema,
                        onSalir = salir,
                        onVolver = if (variosRoles) ({ navController.popBackStack(); Unit }) else null,
                        onReservar = { id -> navController.navigate(Rutas.reservaDe(id)) },
                        onMisReservas = irAMisReservas,
                        onMensaje = mostrarMensaje
                    )
                }
            }
            composable(
                route = Rutas.RESERVA,
                arguments = listOf(navArgument("equipoId") { type = NavType.IntType })
            ) {
                RequiereSesion(sesion.usuario, navController) { usuario ->
                    ReservaScreen(
                        usuario = usuario,
                        onTema = onTema,
                        onSalir = salir,
                        onVolver = { navController.popBackStack() },
                        onInventario = irAInventarioDocente,
                        onMisReservas = irAMisReservas,
                        onReservado = {
                            vibrar()
                            mostrarMensaje("Reserva enviada: queda por aprobar")
                            irAMisReservas()
                        },
                        onVibrar = vibrar
                    )
                }
            }
            composable(Rutas.MIS_RESERVAS) {
                RequiereSesion(sesion.usuario, navController) { usuario ->
                    MisReservasScreen(
                        usuario = usuario,
                        onTema = onTema,
                        onSalir = salir,
                        onVolver = irAInventarioDocente,
                        onInventario = irAInventarioDocente,
                        onMensaje = mostrarMensaje
                    )
                }
            }

            // ---------- Encargado(a) CRA ----------
            composable(Rutas.ENCARGADO) {
                RequiereSesion(sesion.usuario, navController) { usuario ->
                    EncargadoMenuScreen(
                        usuario = usuario,
                        onTema = onTema,
                        onSalir = salir,
                        onIr = { opcion ->
                            val ruta = when (opcion) {
                                OpcionEncargado.APROBAR -> Rutas.APROBAR
                                OpcionEncargado.DEVOLUCION -> Rutas.DEVOLUCION
                                OpcionEncargado.HISTORIAL -> Rutas.HISTORIAL
                                OpcionEncargado.INVENTARIO -> Rutas.INVENTARIO_ENCARGADO
                                OpcionEncargado.AGREGAR -> Rutas.AGREGAR_EQUIPO
                                OpcionEncargado.REPORTES -> Rutas.REPORTES
                            }
                            navController.navigate(ruta)
                        }
                    )
                }
            }
            composable(Rutas.APROBAR) {
                RequiereSesion(sesion.usuario, navController) {
                    AprobarScreen(
                        onTema = onTema,
                        onSalir = salir,
                        onVolver = { navController.popBackStack() },
                        onMensaje = mostrarMensaje
                    )
                }
            }
            composable(Rutas.DEVOLUCION) {
                RequiereSesion(sesion.usuario, navController) {
                    DevolucionScreen(
                        onTema = onTema,
                        onSalir = salir,
                        onVolver = { navController.popBackStack() },
                        onRegistrada = {
                            mostrarMensaje("Devolución registrada")
                            navController.popBackStack()
                        },
                        onMensaje = mostrarMensaje
                    )
                }
            }
            composable(Rutas.HISTORIAL) {
                RequiereSesion(sesion.usuario, navController) {
                    HistorialScreen(
                        onTema = onTema,
                        onSalir = salir,
                        onVolver = { navController.popBackStack() }
                    )
                }
            }
            composable(Rutas.INVENTARIO_ENCARGADO) {
                RequiereSesion(sesion.usuario, navController) {
                    InventarioScreen(
                        modoEncargado = true,
                        onTema = onTema,
                        onSalir = salir,
                        onVolver = { navController.popBackStack() },
                        onMensaje = mostrarMensaje
                    )
                }
            }
            composable(Rutas.AGREGAR_EQUIPO) {
                RequiereSesion(sesion.usuario, navController) {
                    AgregarEquipoScreen(
                        onTema = onTema,
                        onSalir = salir,
                        onVolver = { navController.popBackStack() },
                        onGuardado = {
                            mostrarMensaje("Equipo agregado")
                            navController.popBackStack()
                        },
                        onMensaje = mostrarMensaje
                    )
                }
            }

            // ---------- Reportes (UTP / Administración y Encargado(a)) ----------
            composable(Rutas.REPORTES) {
                RequiereSesion(sesion.usuario, navController) {
                    // Volver: el Encargado(a) vuelve a su menú; quien tiene 2 roles vuelve a elegir rol
                    val puedeVolver = sesion.rol == Rol.ENCARGADO || variosRoles
                    ReportesScreen(
                        onTema = onTema,
                        onSalir = salir,
                        onVolver = if (puedeVolver) ({ navController.popBackStack(); Unit }) else null
                    )
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = if (conBarraInferior) 80.dp else 8.dp)
        )
        AvisoVibracion(
            contador = vibraciones,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 72.dp)
        )
    }
}

/**
 * Si no hay sesión (por ejemplo, Android cerró la app en segundo plano), vuelve al login
 * en vez de mostrar una pantalla sin usuario.
 */
@Composable
private fun RequiereSesion(
    usuario: UsuarioEntity?,
    navController: NavHostController,
    contenido: @Composable (UsuarioEntity) -> Unit
) {
    if (usuario != null) {
        contenido(usuario)
    } else {
        LaunchedEffect(Unit) {
            if (navController.currentDestination?.route != Rutas.LOGIN) {
                navController.navigate(Rutas.LOGIN) { popUpTo(navController.graph.id) { inclusive = true } }
            }
        }
    }
}
