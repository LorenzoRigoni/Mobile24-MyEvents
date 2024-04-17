package com.example.myevents.ui.screens.welcome

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myevents.data.repositories.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class WelcomeState(val user: String)

class WelcomeViewModel (
    private val repository: UserRepository
) : ViewModel() {
    var state by mutableStateOf(WelcomeState(""))
        private set

    fun logout() {
        viewModelScope.launch {
            repository.setLoggedUser("")
        }
    }

    init {
        viewModelScope.launch {
            state = WelcomeState(repository.user.first())
        }
    }
}
