package com.example.myevents.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.map

class SettingsRepository(private val dataStore: DataStore<Preferences>) {
    companion object {
        private val THEME_KEY = stringPreferencesKey("theme")
        private val LANGUAGE_KEY = stringPreferencesKey("language")
        private val REMINDER_TIME_KEY = stringPreferencesKey("reminder_time")
    }

    val theme = dataStore.data.map { it[THEME_KEY] ?: "Light" }
    val language = dataStore.data.map { it[LANGUAGE_KEY] ?: "English" }
    val reminderTime = dataStore.data.map { it[REMINDER_TIME_KEY] ?: "00:00" }

    suspend fun setTheme(value: String) = dataStore.edit { it[THEME_KEY] = value }
    suspend fun setLanguage(value: String) = dataStore.edit { it[LANGUAGE_KEY] = value }
    suspend fun setReminderTime(value: String) = dataStore.edit { it[REMINDER_TIME_KEY] = value }
}
