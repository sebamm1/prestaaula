package com.example.prestaaula.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.prestaaula.data.repository.PrestaAulaRepository

/**
 * Factory de todos los ViewModel de la app (igual que el "ViewModel con Factory" del proyecto Room):
 * le entrega a cada ViewModel el Repository (y a ReservaViewModel, el id del equipo que llega por navegación).
 * Uso en una pantalla: viewModel(factory = AppViewModelProvider.Factory)
 */
object AppViewModelProvider {
    val Factory: ViewModelProvider.Factory = viewModelFactory {
        initializer { SesionViewModel(repositorio()) }
        initializer { InventarioViewModel(repositorio()) }
        initializer { ReservaViewModel(createSavedStateHandle(), repositorio()) }
        initializer { MisReservasViewModel(repositorio()) }
        initializer { EncargadoViewModel(repositorio()) }
        initializer { DevolucionViewModel(repositorio()) }
        initializer { AgregarEquipoViewModel(repositorio()) }
        initializer { ReportesViewModel(repositorio()) }
    }
}

private fun CreationExtras.repositorio(): PrestaAulaRepository =
    PrestaAulaRepository.obtener(this[APPLICATION_KEY] as Application)
