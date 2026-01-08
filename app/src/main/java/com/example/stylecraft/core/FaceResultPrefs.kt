package com.example.stylecraft.core

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.stylecraft.data.api.AppDatabase
import com.example.stylecraft.data.api.ScanHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "face_result_prefs")

object FaceResultPrefs {
    private val KEY_FACE_SHAPE = stringPreferencesKey("face_shape")
    private val KEY_CONFIDENCE = floatPreferencesKey("confidence")
    private val KEY_LAST_IMAGE_PATH = stringPreferencesKey("last_image_path")
    private val KEY_ORIGINAL_IMAGE_PATH = stringPreferencesKey("original_image_path")

    fun faceShapeFlow(context: Context): Flow<String?> =
        context.dataStore.data.map { prefs -> prefs[KEY_FACE_SHAPE] }

    fun confidenceFlow(context: Context): Flow<Float?> =
        context.dataStore.data.map { prefs -> prefs[KEY_CONFIDENCE] }

    fun lastImagePathFlow(context: Context): Flow<String?> =
        context.dataStore.data.map { prefs -> prefs[KEY_LAST_IMAGE_PATH] }
    
    fun originalImagePathFlow(context: Context): Flow<String?> =
        context.dataStore.data.map { prefs -> prefs[KEY_ORIGINAL_IMAGE_PATH] }

    suspend fun saveFaceShape(context: Context, faceShape: String, confidence: Float = 0.9f) {
        context.dataStore.edit { prefs ->
            prefs[KEY_FACE_SHAPE] = faceShape
            prefs[KEY_CONFIDENCE] = confidence
        }

        val repository = ScanHistoryRepository(AppDatabase.getDatabase(context))
        repository.saveScan(faceShape, confidence)
    }

    suspend fun saveLastImagePath(context: Context, path: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_LAST_IMAGE_PATH] = path
        }
    }
    
    suspend fun saveOriginalImagePath(context: Context, path: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ORIGINAL_IMAGE_PATH] = path
        }
    }
    
    /**
     * Save both paths at once - used when initially capturing/uploading an image
     */
    suspend fun saveImagePaths(context: Context, path: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_LAST_IMAGE_PATH] = path
            prefs[KEY_ORIGINAL_IMAGE_PATH] = path
        }
    }
}
