package com.example.myevents.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myevents.data.database.Event
import com.example.myevents.data.repositories.MyEventsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class EventsState(val events: List<Event>)

class EventsViewModel(
    private val repository: MyEventsRepository
) : ViewModel() {
    val state = repository.events.map { EventsState(events = it) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = EventsState(emptyList())
    )

    fun addEvent(event: Event) = viewModelScope.launch {
        repository.upsertEvent(event)
    }

    fun deleteEvent(event: Event) = viewModelScope.launch {
        repository.deleteEvent(event)
    }

    fun checkLogin(username: String, password: String) : Boolean {
        return repository.getUserForLogin(username, password) != null
    }
}
