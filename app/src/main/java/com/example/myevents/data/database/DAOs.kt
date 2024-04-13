package com.example.myevents.data.database

import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

interface UserDAO {
    //Insert here the queries
    @Query("SELECT * FROM user")
    fun getAll(): Flow<List<User>>
    @Upsert
    suspend fun upsert(user: User)

    @Delete
    suspend fun delete(user: User)
}

interface EventDAO {
    //Insert here the queries
    @Query("SELECT * FROM user")
    fun getAll(): Flow<List<Event>>

    @Upsert
    suspend fun upsert(event: Event)

    @Delete
    suspend fun delete(event: Event)
}

interface NotificationDAO {
    //Insert here the queries
    @Query("SELECT * FROM user")
    fun getAll(): Flow<List<Notification>>

    @Upsert
    suspend fun upsert(notification: Notification)

    @Delete
    suspend fun delete(notification: Notification)
}