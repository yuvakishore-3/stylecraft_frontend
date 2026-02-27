package com.example.stylecraft.core

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.themeDataStore by preferencesDataStore(name = "theme_preferences")

enum class AppTheme {
    LIGHT, DARK, SYSTEM
}

object ThemePrefs {
    private val THEME_KEY = stringPreferencesKey("app_theme")

    fun themeFlow(context: Context): Flow<AppTheme> {
        return context.themeDataStore.data.map { preferences ->
            val themeName = preferences[THEME_KEY] ?: AppTheme.SYSTEM.name
            try {
                AppTheme.valueOf(themeName)
            } catch (e: Exception) {
                AppTheme.SYSTEM
            }
        }
    }

    suspend fun setTheme(context: Context, theme: AppTheme) {
        context.themeDataStore.edit { preferences ->
            preferences[THEME_KEY] = theme.name
        }
    }
}
