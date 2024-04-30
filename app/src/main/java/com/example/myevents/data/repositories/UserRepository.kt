package com.example.myevents.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.myevents.data.database.User
import com.example.myevents.data.database.UserDAO
import kotlinx.coroutines.flow.map

class UserRepository(
    private val dataStore: DataStore<Preferences>,
    private val userDAO: UserDAO,
) {
    companion object {
        private val USER_KEY = stringPreferencesKey("user")
        private val LOGGED_KEY = booleanPreferencesKey("isLogged")
    }

    val user = dataStore.data.map { it[USER_KEY] ?: "" }
    val isLogged = dataStore.data.map { it[LOGGED_KEY] ?: false }

    suspend fun setLoggedUser(value: String) = dataStore.edit { it[USER_KEY] = value }
    suspend fun setIsLogged(value: Boolean) = dataStore.edit { it[LOGGED_KEY] = value }

    suspend fun getUserForLogin(username: String, password: String) : User? {
        return userDAO.getUserForLogin(username, password)
    }

    suspend fun getUserByUsername(username: String) : User? {
        return userDAO.getUserByUsername(username)
    }

    suspend fun getImageUriByUsername(username: String): String? {
        return userDAO.getImageUriByUsername(username)
    }

    suspend fun upsertUser(user: User) = userDAO.upsert(user)
    suspend fun deleteUser(user: User) = userDAO.delete(user)
}
