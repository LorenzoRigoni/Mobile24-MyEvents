package com.example.myevents.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDAO {
    @Query("SELECT * FROM user WHERE username = :username AND password = :password")
    suspend fun getUserForLogin(username: String, password: String): User?
    @Query("SELECT * FROM user WHERE username = :username")
    suspend fun getUserByUsername(username: String): User?
    @Query("SELECT imageUri FROM user WHERE username = :username")
    suspend fun getImageUriByUsername(username: String): String?
    @Upsert
    suspend fun upsert(user: User)
    @Delete
    suspend fun delete(user: User)
}

@Dao
interface EventDAO {
    @Query("SELECT * FROM event WHERE username = :username AND date >= date('now') ORDER BY date ASC")
    fun getEventsOfUserFromToday(username: String): Flow<List<Event>>
    @Upsert
    suspend fun upsert(event: Event)
    @Delete
    suspend fun delete(event: Event)
}

@Dao
interface NotificationDAO {
    @Query("SELECT * FROM notification")
    fun getAll(): Flow<List<Notification>>
    @Upsert
    suspend fun upsert(notification: Notification)
    @Delete
    suspend fun delete(notification: Notification)
}