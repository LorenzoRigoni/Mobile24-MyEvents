package com.example.myevents.ui.screens.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myevents.data.repositories.SettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class SettingsState(val username: String)

class SettingsViewModel (
    private val repository: SettingsRepository
) : ViewModel() {
    var state by mutableStateOf(SettingsState(""))
        private set

    fun setUsername(value: String) {
        state = SettingsState(value)
        viewModelScope.launch { repository.setUsername(value) }
    }

    init {
        viewModelScope.launch {
            state = SettingsState(repository.username.first())
        }
    }
}
