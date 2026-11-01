package com.example.prestaaula.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Un solo DataStore para toda la app (archivo "ajustes"). */
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ajustes")

/** Las tres opciones que ve el usuario (clase "Theme Switcher con DataStore"). */
enum class ThemeMode { LIGHT, DARK, SYSTEM }

object ThemePreferences {
    private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")

    /** Lee la preferencia guardada; si no hay nada, usa SYSTEM. */
    fun getThemeMode(dataStore: DataStore<Preferences>): Flow<ThemeMode> =
        dataStore.data.map { preferences ->
            val modeName = preferences[THEME_MODE_KEY] ?: ThemeMode.SYSTEM.name
            runCatching { ThemeMode.valueOf(modeName) }.getOrDefault(ThemeMode.SYSTEM)
        }

    suspend fun setThemeMode(dataStore: DataStore<Preferences>, mode: ThemeMode) {
        dataStore.edit { preferences -> preferences[THEME_MODE_KEY] = mode.name }
    }
}
