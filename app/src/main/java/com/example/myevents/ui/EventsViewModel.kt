package com.example.myevents.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myevents.data.database.Event
import com.example.myevents.data.database.Notification
import com.example.myevents.data.repositories.MyEventsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class EventsState(val events: List<Event>)
data class NotificationsState(val notifications: List<Notification>)

class EventsViewModel(
    private val repository: MyEventsRepository
) : ViewModel() {

    val state = MutableStateFlow(EventsState(emptyList()))
    val notifState = MutableStateFlow(NotificationsState(emptyList()))

    val eventsToDelete: MutableList<Int> = mutableListOf()

    init {
        updateEvents(FilterEnum.SHOW_FUTURE_EVENTS)
        updateNotifications()
    }


    fun updateEvents(filter: FilterEnum) {
        viewModelScope.launch {
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

    fun updateNotifications() {
        viewModelScope.launch {
            repository.allUserNotifications(repository.user.first()).collect { notifications ->
                notifState.value = NotificationsState(notifications)
            }
        }
    }

    fun generateNotification(notificationText: String) {
        viewModelScope.launch {
            repository.upsertNotification(
                Notification(
                    0,
                    repository.user.first(),
                    notificationText,
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                )
            )
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
                val event = repository.getEventFromId(id)
                repository.deleteEventFromId(id)
                repository.upsertNotification(
                    Notification(
                        0,
                        event!!.username,
                        "${event.title};delete",
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    )
                )
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
