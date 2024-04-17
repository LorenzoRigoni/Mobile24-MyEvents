package com.example.myevents.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.map

class UserRepository(private val dataStore: DataStore<Preferences>) {
    companion object {
        private val USER_KEY = stringPreferencesKey("user")
        private val LOGGED_KEY = booleanPreferencesKey("isLogged")
    }

    val user = dataStore.data.map { it[USER_KEY] ?: "" }
    val isLogged = dataStore.data.map { it[LOGGED_KEY] ?: false }

    suspend fun setLoggedUser(value: String) = dataStore.edit { it[USER_KEY] = value }
    suspend fun setIsLogged(value: Boolean) = dataStore.edit { it[LOGGED_KEY] = value }
}
