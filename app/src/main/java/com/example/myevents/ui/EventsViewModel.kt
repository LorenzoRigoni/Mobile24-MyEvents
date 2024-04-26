package com.example.myevents.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myevents.data.database.Event
import com.example.myevents.data.repositories.MyEventsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class EventsState(val events: List<Event>)

class EventsViewModel(
    private val repository: MyEventsRepository
) : ViewModel() {

    var state by mutableStateOf(EventsState(emptyList()))
        private set

    init {
        viewModelScope.launch {
            state = repository.eventsOfUserFromToday(repository.user.first()).map { EventsState(events = it) }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = EventsState(emptyList())
            ).value
        }
    }

    fun addEvent(event: Event) = viewModelScope.launch {
        repository.upsertEvent(event)
    }

    fun deleteEvent(event: Event) = viewModelScope.launch {
        repository.deleteEvent(event)
    }
}
