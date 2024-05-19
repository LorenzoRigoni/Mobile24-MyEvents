package com.example.myevents.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.myevents.data.database.Event
import com.example.myevents.data.database.EventDAO
import com.example.myevents.data.database.Notification
import com.example.myevents.data.database.NotificationDAO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MyEventsRepository(
    dataStore: DataStore<Preferences>,
    private val eventDAO: EventDAO,
    private val notificationDAO: NotificationDAO
) {
    companion object {
        private val USER_KEY = stringPreferencesKey("user")
    }

    val user = dataStore.data.map { it[USER_KEY] ?: "" }

    fun eventsOfUserFromToday(username: String) : Flow<List<Event>> {
        return eventDAO.getEventsOfUserFromToday(username)
    }

    fun allEventsOfUser(username: String): Flow<List<Event>> {
        return eventDAO.getAllEventsOfUser(username)
    }

    fun pastEventsOfUser(username: String): Flow<List<Event>> {
        return eventDAO.getPastEventsOfUser(username)
    }

    fun favouritesEventsOfUser(username: String): Flow<List<Event>> {
        return eventDAO.getFavouritesEventsOfUser(username)
    }

    suspend fun upsertEvent(event: Event) = eventDAO.upsert(event)
    suspend fun deleteEvent(event: Event) = eventDAO.delete(event)
    suspend fun deleteEventFromId(eventId: Int) = eventDAO.deleteEventFromId(eventId)
    suspend fun updateIsFavourite(isFavourite: Boolean, eventId: Int) = eventDAO.updateIsFavourite(isFavourite, eventId)

    val notifications: Flow<List<Notification>> = notificationDAO.getAll()
    suspend fun upsertNotification(notification: Notification) =
        notificationDAO.upsert(notification)

    suspend fun deleteNotification(notification: Notification) =
        notificationDAO.delete(notification)
}