package com.example.myevents.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myevents.data.database.User
import com.example.myevents.data.repositories.UserRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class UserState(val user: String, val isLogged: Boolean)

interface UserActions {
    fun addUser(user: User): Job
    fun removeUser(user: User): Job
}

class UserViewModel (
    private val repository: UserRepository
) : ViewModel() {
    var state by mutableStateOf(UserState("", false))
        private set
    var user by mutableStateOf<User?>(null)
        private set
    var imageUri by mutableStateOf<String?>(null)
        private set

    init {
        viewModelScope.launch {
            state = UserState(repository.user.first(), repository.isLogged.first())
            if (state.isLogged) {
                user = repository.getUserByUsername(state.user)
            }
        }
    }

    fun setLoggedUser(value: String, rememberMe: Boolean) {
        viewModelScope.launch {
            repository.setLoggedUser(value)
            repository.setIsLogged(rememberMe)
        }
        state = UserState(value, true)
    }

    fun logout() {
        viewModelScope.launch {
            repository.setLoggedUser("")
            repository.setIsLogged(false)
        }
        state = UserState("", false)
        user = null
    }

    fun checkLogin(username: String, password: String) : Boolean {
        viewModelScope.launch {
            user = repository.getUserForLogin(username, password)
        }
        return user != null
    }

    fun isUsernameAlreadyTaken(username: String) : Boolean {
        viewModelScope.launch {
            user = repository.getUserByUsername(username)
        }
        return user != null
    }

    fun getImageUri(username: String) : String? {
        viewModelScope.launch {
            imageUri = repository.getImageUriByUsername(username)
        }
        return imageUri
    }

    val actions = object : UserActions {
        override fun addUser(user: User) = viewModelScope.launch {
            repository.upsertUser(user)
        }

        override fun removeUser(user: User) = viewModelScope.launch {
            repository.deleteUser(user)
        }
    }
}
