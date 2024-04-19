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

data class UserState(val user: String)

interface UserActions {
    fun addUser(user: User): Job
    fun removeUser(user: User): Job
}

class UserViewModel (
    private val repository: UserRepository
) : ViewModel() {
    var state by mutableStateOf(UserState(""))
        private set
    var user by mutableStateOf<User?>(null)
        private set

    init {
        viewModelScope.launch {
            state = UserState(repository.user.first())
            if (state.user.isNotEmpty()) {
                user = repository.getUserBySharedPreferencesSave(state.user)
            }
        }
    }

    fun setLoggedUser(value: String) {
        viewModelScope.launch { repository.setLoggedUser(value) }
        state = UserState(value)
    }

    fun logout() {
        viewModelScope.launch { repository.setLoggedUser("") }
        state = UserState("")
        user = null
    }

    fun checkLogin(username: String, password: String) : Boolean {
        user = repository.getUserForLogin(username, password)
        return user != null
    }

    fun isUsernameAlreadyTaken(username: String) : Boolean {
        user = repository.getUserBySharedPreferencesSave(username)
        return user != null
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
