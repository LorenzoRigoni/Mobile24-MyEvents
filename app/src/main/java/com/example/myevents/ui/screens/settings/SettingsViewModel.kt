package com.example.myevents.ui.screens.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myevents.data.repositories.SettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class SettingsPreferences(var theme: String, var language: String, var reminderTime: String)

class SettingsViewModel (
    private val repository: SettingsRepository
) : ViewModel() {

    private val preferences by mutableStateOf(SettingsPreferences("", "", ""))

    init {
        viewModelScope.launch {
            preferences.theme = repository.theme.first()
            preferences.language = repository.language.first()
            preferences.reminderTime = repository.reminderTime.first()
        }
    }

    fun getTheme() = preferences.theme

    fun setTheme(value: String) {
        preferences.theme = value
        viewModelScope.launch { repository.setTheme(value) }
    }

    fun setLanguage(value: String) {
        preferences.language = value
        viewModelScope.launch { repository.setLanguage(value) }
    }

    fun setReminderTime(value: String) {
        preferences.reminderTime = value
        viewModelScope.launch { repository.setReminderTime(value) }
    }
}
