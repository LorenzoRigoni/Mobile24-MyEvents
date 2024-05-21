package com.example.myevents.ui.screens.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myevents.data.repositories.SettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class SettingsPreferences(val theme: String, val language: String, val reminderTime: String)

class SettingsViewModel (
    private val repository: SettingsRepository
) : ViewModel() {

    var preferences by mutableStateOf(SettingsPreferences("", "", ""))
        private set

    init {
        viewModelScope.launch {
            preferences = SettingsPreferences(
                repository.theme.first(),
                repository.language.first(),
                repository.reminderTime.first()
            )
        }
    }

    fun setTheme(value: String) {
        preferences = preferences.copy(theme = value)
        viewModelScope.launch { repository.setTheme(value) }
    }

    fun setLanguage(value: String) {
        preferences = preferences.copy(language = value)
        viewModelScope.launch { repository.setLanguage(value) }
    }

    fun setReminderTime(value: String) {
        preferences = preferences.copy(reminderTime = value)
        viewModelScope.launch { repository.setReminderTime(value) }
    }
}
