package com.etan.nearbynav.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

enum class AppTheme {
    VINTAGE, CYBERPUNK, NIGHT, NAUTICAL, GIRLYPOP
}

class SettingsRepository(private val context: Context) {

    companion object {
        val THEME_KEY = stringPreferencesKey("app_theme")
    }

    val themeFlow: Flow<AppTheme> = context.dataStore.data.map { prefs ->
        val stored = prefs[THEME_KEY] ?: AppTheme.VINTAGE.name
        try {
            AppTheme.valueOf(stored)
        } catch (e: IllegalArgumentException) {
            // Saved theme no longer exists — fall back to default
            AppTheme.VINTAGE
        }
    }

    suspend fun setTheme(theme: AppTheme) {
        context.dataStore.edit { prefs ->
            prefs[THEME_KEY] = theme.name
        }
    }
}