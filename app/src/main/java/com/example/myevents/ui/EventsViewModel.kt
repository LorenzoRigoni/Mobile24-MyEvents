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

    val eventsToDelete: MutableList<Int> = mutableListOf()

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
        if (eventsToDelete.isEmpty()) return
        viewModelScope.launch {
            val idsToDelete = eventsToDelete.toList()
            idsToDelete.forEach { id ->
                repository.deleteEventFromId(id)
            }
        }
        eventsToDelete.clear()
    }

    fun updateIsFavourite(isFavourite: Boolean, eventId: Int) {
        viewModelScope.launch {
            repository.updateIsFavourite(isFavourite, eventId)
        }
    }
}
