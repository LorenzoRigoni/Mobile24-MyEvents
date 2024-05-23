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
        updateEvents(FilterEnum.SHOW_FUTURE_EVENTS)
    }


    fun updateEvents(filter: FilterEnum) {
        viewModelScope.launch {
            //val username = if (isInit) repository.user.first() else ""
            when (filter) {
                FilterEnum.SHOW_FUTURE_EVENTS -> {
                    repository.eventsOfUserFromToday(repository.user.first()).collect { events ->
                        state.value = EventsState(events)
                    }
                }
                FilterEnum.SHOW_ALL_EVENTS -> {
                    repository.allEventsOfUser(repository.user.first()).collect {events ->
                        state.value = EventsState(events)
                    }
                }
                FilterEnum.SHOW_PAST_EVENTS -> {
                    repository.pastEventsOfUser(repository.user.first()).collect {events ->
                        state.value = EventsState(events)
                    }
                }
                FilterEnum.SHOW_FAVOURITES_EVENTS -> {
                    repository.favouritesEventsOfUser(repository.user.first()).collect {events ->
                        state.value = EventsState(events)
                    }
                }
            }
        }
    }

    fun getNextEvent(): Event? {
        return state.value.events.minByOrNull { it.date }
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
