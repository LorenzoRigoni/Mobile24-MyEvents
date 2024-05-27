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

    val allEventsState = MutableStateFlow(EventsState(emptyList()))
    val futureEventsState = MutableStateFlow(EventsState(emptyList()))
    val state = MutableStateFlow(EventsState(emptyList()))

    val eventsToDelete: MutableList<Int> = mutableListOf()

    val notifState = MutableStateFlow(NotificationsState(emptyList()))
    val notificationBadges = MutableStateFlow(0)

    val filter = MutableStateFlow(FilterEnum.SHOW_FUTURE_EVENTS)

    init {
        updateEvents(filter.value)
        updateNotifications()
        updateAllEvents()
        updateFutureEvents()
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

    fun updateAllEvents() {
        viewModelScope.launch {
            repository.allEventsOfUser(repository.user.first()).collect { events ->
                allEventsState.value = EventsState(events)
            }
        }
    }

    fun updateFutureEvents() {
        viewModelScope.launch {
            repository.eventsOfUserFromToday(repository.user.first()).collect { events ->
                futureEventsState.value = EventsState(events)
            }
        }
    }

    fun updateIsSentNotification(notification: Notification) {
        viewModelScope.launch {
            repository.upsertNotification(
                Notification(
                    notification.notificationID,
                    notification.username,
                    notification.notificationText,
                    notification.date,
                    true
                )
            )
        }
    }

    fun generateNotification(notificationText: String) {
        viewModelScope.launch {
            repository.upsertNotification(
                Notification(
                    0,
                    repository.user.first(),
                    notificationText,
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    false
                )
            )
            incrementNotificationBadge()
        }
    }

    fun incrementNotificationBadge() {
        notificationBadges.value++
    }

    fun resetNotificationBadges() {
        notificationBadges.value = 0
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
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        false
                    )
                )
                incrementNotificationBadge()
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
