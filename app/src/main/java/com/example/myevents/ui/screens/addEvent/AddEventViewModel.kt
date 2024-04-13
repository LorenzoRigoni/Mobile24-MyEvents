package com.example.myevents.ui.screens.addEvent

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AddEventState(
    val destination: String = "",
    val date: String = "",
    val description: String = "",
)

interface AddEventActions {
    fun setDestination(title: String)
    fun setDate(date: String)
    fun setDescription(description: String)
}

class AddEventViewModel : ViewModel() {
    private val _state = MutableStateFlow(AddEventState())
    val state = _state.asStateFlow()

    val actions = object : AddEventActions {
        override fun setDestination(title: String) =
            _state.update { it.copy(destination = title) }

        override fun setDate(date: String) =
            _state.update { it.copy(date = date) }

        override fun setDescription(description: String) =
            _state.update { it.copy(description = description) }
    }
}
