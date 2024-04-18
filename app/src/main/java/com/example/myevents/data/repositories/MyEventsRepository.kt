package com.example.myevents.data.repositories

import com.example.myevents.data.database.Event
import com.example.myevents.data.database.EventDAO
import com.example.myevents.data.database.Notification
import com.example.myevents.data.database.NotificationDAO
import com.example.myevents.data.database.User
import com.example.myevents.data.database.UserDAO
import kotlinx.coroutines.flow.Flow

class MyEventsRepository(
    private val userDAO: UserDAO,
    private val eventDAO: EventDAO,
    private val notificationDAO: NotificationDAO
) {
    fun getUserForLogin(username: String, password: String) : User? {
        return userDAO.getUserForLogin(username, password)
    }
    suspend fun upsertUser(user: User) = userDAO.upsert(user)
    suspend fun deleteUser(user: User) = userDAO.delete(user)

    val events: Flow<List<Event>> = eventDAO.getAll()
    suspend fun upsertEvent(event: Event) = eventDAO.upsert(event)
    suspend fun deleteEvent(event: Event) = eventDAO.delete(event)

    val notifications: Flow<List<Notification>> = notificationDAO.getAll()
    suspend fun upsertNotification(notification: Notification) =
        notificationDAO.upsert(notification)

    suspend fun deleteNotification(notification: Notification) =
        notificationDAO.delete(notification)
}