package com.example.myevents.ui.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myevents.data.repositories.UserRepository
import kotlinx.coroutines.launch

data class LoginState(val tmp: String)

class LoginViewModel (
    private val repository: UserRepository
) : ViewModel() {

    fun setLoggedUser(value: String) {
        viewModelScope.launch { repository.setLoggedUser(value) }
    }
}
