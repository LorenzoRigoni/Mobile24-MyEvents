package com.example.myevents.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myevents.data.database.Event
import com.example.myevents.data.repositories.MyEventsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class EventsState(val events: List<Event>)

class EventsViewModel(
    private val repository: MyEventsRepository
) : ViewModel() {

    val state = MutableStateFlow(EventsState(emptyList()))

    val eventToDelete: List<Int> = emptyList()

    init {
        updateEvents("")
    }

    fun updateEvents(name: String) {
        viewModelScope.launch {
            val username = if (name == "") repository.user.first() else name
            repository.eventsOfUserFromToday(username).collect { events ->
                state.value = EventsState(events)
            }
        }
    }

    fun deleteEventsFromListOfIds() {
        viewModelScope.launch {
            eventToDelete.forEach { id ->
                repository.deleteEventFromId(id)
            }
        }
    }
}
