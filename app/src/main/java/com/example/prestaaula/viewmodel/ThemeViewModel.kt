package com.example.prestaaula.viewmodel

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.prestaaula.data.preferences.ThemeMode
import com.example.prestaaula.data.preferences.ThemePreferences
import com.example.prestaaula.data.preferences.dataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Guarda y expone el tema elegido (Claro / Oscuro / Sistema). */
class ThemeViewModel(private val dataStore: DataStore<Preferences>) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = ThemePreferences.getThemeMode(dataStore)
        .stateIn(viewModelScope, SharingStarted.Eagerly, ThemeMode.SYSTEM)

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { ThemePreferences.setThemeMode(dataStore, mode) }
    }

    companion object {
        /** Factory: le entrega el DataStore al ViewModel (igual que el ViewModel con Factory del proyecto Room). */
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as Application
                ThemeViewModel(app.dataStore)
            }
        }
    }
}
