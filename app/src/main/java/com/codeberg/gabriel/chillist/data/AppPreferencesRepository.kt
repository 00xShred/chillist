package com.codeberg.gabriel.chillist.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val Context.dataStore by preferencesDataStore(name = "chillist_settings")

class AppPreferencesRepository(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    companion object {
        private val WIDGET_PREFS_KEY = stringPreferencesKey("widget_preferences")
    }

    val widgetPreferencesFlow: Flow<WidgetPreferences> = context.dataStore.data.map { preferences ->
        val jsonStr = preferences[WIDGET_PREFS_KEY]
        if (jsonStr != null) {
            try {
                json.decodeFromString<WidgetPreferences>(jsonStr)
            } catch (e: Exception) {
                WidgetPreferences()
            }
        } else {
            WidgetPreferences()
        }
    }

    suspend fun updatePreferences(transform: (WidgetPreferences) -> WidgetPreferences) {
        context.dataStore.edit { preferences ->
            val currentJsonStr = preferences[WIDGET_PREFS_KEY]
            val current = if (currentJsonStr != null) {
                try {
                    json.decodeFromString<WidgetPreferences>(currentJsonStr)
                } catch (e: Exception) {
                    WidgetPreferences()
                }
            } else {
                WidgetPreferences()
            }
            val updated = transform(current)
            preferences[WIDGET_PREFS_KEY] = json.encodeToString(updated)
        }
    }
}
