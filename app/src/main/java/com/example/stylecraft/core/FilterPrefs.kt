package com.example.stylecraft.core

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.filterDataStore by preferencesDataStore(name = "filter_settings")

data class FilterSettings(
    val brightness: Float = 0f,
    val contrast: Float = 0f,
    val saturation: Float = 0f,
    val highlight: Float = 0f,
    val shadow: Float = 0f,
    val colorTint: Long? = null // Color as Long (ARGB)
)

object FilterPrefs {
    private val BRIGHTNESS_KEY = floatPreferencesKey("brightness")
    private val CONTRAST_KEY = floatPreferencesKey("contrast")
    private val SATURATION_KEY = floatPreferencesKey("saturation")
    private val HIGHLIGHT_KEY = floatPreferencesKey("highlight")
    private val SHADOW_KEY = floatPreferencesKey("shadow")
    private val COLOR_TINT_KEY = longPreferencesKey("color_tint")

    fun filterSettingsFlow(context: Context): Flow<FilterSettings> {
        return context.filterDataStore.data.map { preferences ->
            FilterSettings(
                brightness = preferences[BRIGHTNESS_KEY] ?: 0f,
                contrast = preferences[CONTRAST_KEY] ?: 0f,
                saturation = preferences[SATURATION_KEY] ?: 0f,
                highlight = preferences[HIGHLIGHT_KEY] ?: 0f,
                shadow = preferences[SHADOW_KEY] ?: 0f,
                colorTint = preferences[COLOR_TINT_KEY]
            )
        }
    }

    suspend fun saveFilterSettings(context: Context, settings: FilterSettings) {
        context.filterDataStore.edit { preferences ->
            preferences[BRIGHTNESS_KEY] = settings.brightness
            preferences[CONTRAST_KEY] = settings.contrast
            preferences[SATURATION_KEY] = settings.saturation
            preferences[HIGHLIGHT_KEY] = settings.highlight
            preferences[SHADOW_KEY] = settings.shadow
            if (settings.colorTint != null) {
                preferences[COLOR_TINT_KEY] = settings.colorTint
            } else {
                preferences.remove(COLOR_TINT_KEY)
            }
        }
    }

    suspend fun clearFilterSettings(context: Context) {
        context.filterDataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
