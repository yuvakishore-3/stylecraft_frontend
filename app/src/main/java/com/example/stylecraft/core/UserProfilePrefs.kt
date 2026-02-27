package com.example.stylecraft.core

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.userDataStore by preferencesDataStore(name = "user_profile_prefs")

object UserProfilePrefs {
    private val KEY_FULL_NAME = stringPreferencesKey("full_name")
    private val KEY_PHONE = stringPreferencesKey("phone")
    private val KEY_EMAIL = stringPreferencesKey("email")
    private val KEY_DOB = stringPreferencesKey("dob")

    fun fullNameFlow(context: Context): Flow<String> =
        context.userDataStore.data.map { prefs -> prefs[KEY_FULL_NAME] ?: "John Doe" }

    fun phoneFlow(context: Context): Flow<String> =
        context.userDataStore.data.map { prefs -> prefs[KEY_PHONE] ?: "+123 567 89000" }

    fun emailFlow(context: Context): Flow<String> =
        context.userDataStore.data.map { prefs -> prefs[KEY_EMAIL] ?: "johndoe@example.com" }

    fun dobFlow(context: Context): Flow<String> =
        context.userDataStore.data.map { prefs -> prefs[KEY_DOB] ?: "DD / MM / YYY" }

    suspend fun updateProfile(
        context: Context,
        fullName: String,
        phone: String,
        email: String,
        dob: String
    ) {
        context.userDataStore.edit { prefs ->
            prefs[KEY_FULL_NAME] = fullName
            prefs[KEY_PHONE] = phone
            prefs[KEY_EMAIL] = email
            prefs[KEY_DOB] = dob
        }
    }
}
