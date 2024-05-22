package com.example.myevents.data.repositories

import android.content.ContentResolver
import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.myevents.data.database.User
import com.example.myevents.data.database.UserDAO
import com.example.myevents.utils.saveImageToStorage
import kotlinx.coroutines.flow.map

class UserRepository(
    private val dataStore: DataStore<Preferences>,
    private val userDAO: UserDAO,
    private val contentResolver: ContentResolver
) {
    companion object {
        private val USER_KEY = stringPreferencesKey("user")
        private val LOGGED_KEY = booleanPreferencesKey("isLogged")
        private val BIOMETRIC_KEY = stringPreferencesKey("bioUser")
        private val BIOPASSWORD_KEY = stringPreferencesKey("bioPassword")
    }

    val user = dataStore.data.map { it[USER_KEY] ?: "" }
    val isLogged = dataStore.data.map { it[LOGGED_KEY] ?: false }
    val bioUser = dataStore.data.map { it[BIOMETRIC_KEY] ?: "" }
    val bioPassword = dataStore.data.map { it[BIOPASSWORD_KEY] ?: "" }

    suspend fun setLoggedUser(value: String) = dataStore.edit { it[USER_KEY] = value }
    suspend fun setIsLogged(value: Boolean) = dataStore.edit { it[LOGGED_KEY] = value }
    suspend fun setBiometricUser(value: String) = dataStore.edit { it[BIOMETRIC_KEY] = value }
    suspend fun setBiometricPassword(value: String) = dataStore.edit { it[BIOPASSWORD_KEY] = value }

    suspend fun getUserForLogin(username: String, password: String) : User? {
        return userDAO.getUserForLogin(username, password)
    }

    suspend fun getUserByUsername(username: String) : User? {
        return userDAO.getUserByUsername(username)
    }

    suspend fun getImageUriByUsername(username: String): String? {
        return userDAO.getImageUriByUsername(username)
    }

    suspend fun upsertUser(user: User) {
        if (user.imageUri?.isNotEmpty() == true) {
            val imageUri = saveImageToStorage(
                Uri.parse(user.imageUri),
                contentResolver,
                "MyEvents_User${user.username}"
            )
            userDAO.upsert(user.copy(imageUri = imageUri.toString()))
        } else {
            userDAO.upsert(user)
        }
    }
    suspend fun deleteUser(user: User) = userDAO.delete(user)
}
