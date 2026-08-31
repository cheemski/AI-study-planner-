package com.example.studyplannerapp.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Requires the DataStore Preferences dependency in app/build.gradle:
//   implementation "androidx.datastore:datastore-preferences:1.1.1"
// (version number may need bumping to whatever's current when you sync)
private val Context.settingsDataStore by preferencesDataStore(name = "app_settings")

/**
 * Single source of truth for app-wide settings that need to survive restarts.
 * Both the top-level theme wrapper (MainActivity) and ProfileScreen read from
 * this same store, so a change made in one place is reflected everywhere
 * without needing a shared ViewModel instance across navigation scopes.
 */
object SettingsDataStore {
    private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
    private val NOTIFICATIONS_KEY = booleanPreferencesKey("notifications_enabled")

    fun darkModeFlow(context: Context): Flow<Boolean> =
        context.settingsDataStore.data.map { prefs -> prefs[DARK_MODE_KEY] ?: false }

    suspend fun setDarkMode(context: Context, enabled: Boolean) {
        context.settingsDataStore.edit { prefs -> prefs[DARK_MODE_KEY] = enabled }
    }

    fun notificationsFlow(context: Context): Flow<Boolean> =
        context.settingsDataStore.data.map { prefs -> prefs[NOTIFICATIONS_KEY] ?: true }

    suspend fun setNotifications(context: Context, enabled: Boolean) {
        context.settingsDataStore.edit { prefs -> prefs[NOTIFICATIONS_KEY] = enabled }
    }
}
