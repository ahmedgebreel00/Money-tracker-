package com.example.moneytracker.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

enum class DarkMode { SYSTEM, LIGHT, DARK }

data class Settings(
    val themeId: String = "ocean",
    val darkMode: DarkMode = DarkMode.SYSTEM,
    val dynamicColor: Boolean = false,
    val currency: String = "EGP"
)

class SettingsRepository(private val context: Context) {

    private object Keys {
        val THEME = stringPreferencesKey("theme")
        val DARK_MODE = stringPreferencesKey("dark_mode")
        val DYNAMIC = booleanPreferencesKey("dynamic_color")
        val CURRENCY = stringPreferencesKey("currency")
        val SEEDED = booleanPreferencesKey("categories_seeded")
    }

    val settings: Flow<Settings> = context.dataStore.data.map { prefs ->
        Settings(
            themeId = prefs[Keys.THEME] ?: "ocean",
            darkMode = prefs[Keys.DARK_MODE]
                ?.let { name -> DarkMode.values().firstOrNull { it.name == name } }
                ?: DarkMode.SYSTEM,
            dynamicColor = prefs[Keys.DYNAMIC] ?: false,
            currency = prefs[Keys.CURRENCY] ?: "EGP"
        )
    }

    suspend fun setTheme(id: String) {
        context.dataStore.edit { it[Keys.THEME] = id }
    }

    suspend fun setDarkMode(mode: DarkMode) {
        context.dataStore.edit { it[Keys.DARK_MODE] = mode.name }
    }

    suspend fun setDynamicColor(enabled: Boolean) {
        context.dataStore.edit { it[Keys.DYNAMIC] = enabled }
    }

    suspend fun setCurrency(code: String) {
        context.dataStore.edit { it[Keys.CURRENCY] = code }
    }

    suspend fun isSeeded(): Boolean = context.dataStore.data.first()[Keys.SEEDED] ?: false

    suspend fun markSeeded() {
        context.dataStore.edit { it[Keys.SEEDED] = true }
    }
}
