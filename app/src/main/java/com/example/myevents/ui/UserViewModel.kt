package com.example.myevents.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myevents.data.repositories.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class UserState(val user: String)

class UserViewModel (
    private val repository: UserRepository
) : ViewModel() {
    var state by mutableStateOf(UserState(""))
        private set

    fun setLoggedUser(value: String) {
        viewModelScope.launch { repository.setLoggedUser(value) }
        state = UserState(value)
    }

    fun logout() {
        viewModelScope.launch { repository.setLoggedUser("") }
        state = UserState("")
    }

    init {
        viewModelScope.launch {
            state = UserState(repository.user.first())
        }
    }
}