package com.example.prestaaula

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.prestaaula.data.preferences.ThemeMode
import com.example.prestaaula.ui.components.TemaDialog
import com.example.prestaaula.ui.navigation.AppNavigation
import com.example.prestaaula.ui.theme.PrestaAulaTheme
import com.example.prestaaula.viewmodel.AppViewModelProvider
import com.example.prestaaula.viewmodel.SesionViewModel
import com.example.prestaaula.viewmodel.ThemeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Tema elegido (Claro / Oscuro / Sistema), guardado con DataStore
            val themeViewModel: ThemeViewModel = viewModel(factory = ThemeViewModel.Factory)
            val modo by themeViewModel.themeMode.collectAsState()
            val oscuro = when (modo) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            // Sesión: quién entró y con qué rol (el rol define el color de acento)
            val sesionViewModel: SesionViewModel = viewModel(factory = AppViewModelProvider.Factory)
            val sesion by sesionViewModel.sesion.collectAsState()

            var mostrarTema by rememberSaveable { mutableStateOf(false) }

            // Íconos de la barra de estado claros u oscuros según el tema de la app
            DisposableEffect(oscuro) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { oscuro },
                    navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { oscuro }
                )
                onDispose { }
            }

            PrestaAulaTheme(darkTheme = oscuro, rol = sesion.rol) {
                AppNavigation(
                    sesionViewModel = sesionViewModel,
                    onTema = { mostrarTema = true }
                )
                if (mostrarTema) {
                    TemaDialog(
                        modoActual = modo,
                        onElegir = { themeViewModel.setThemeMode(it) },
                        onCerrar = { mostrarTema = false }
                    )
                }
            }
        }
    }
}
