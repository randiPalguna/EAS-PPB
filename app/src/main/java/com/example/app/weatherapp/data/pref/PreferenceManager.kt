package com.example.app.weatherapp.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferenceManager(private val context: Context) {

    companion object {
        val DEFAULT_CITY = stringPreferencesKey("default_city")
    }

    val defaultCity: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[DEFAULT_CITY] ?: "London"
    }

    suspend fun setDefaultCity(city: String) {
        context.dataStore.edit { preferences ->
            preferences[DEFAULT_CITY] = city
        }
    }
}
