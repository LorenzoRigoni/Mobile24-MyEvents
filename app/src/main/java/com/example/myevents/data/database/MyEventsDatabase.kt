package com.example.myevents.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [User::class, Event::class, Notification::class], version = 7)
abstract class MyEventsDatabase : RoomDatabase() {
    abstract fun userDAO() : UserDAO
    abstract fun eventDAO() : EventDAO
    abstract fun notificationDAO() : NotificationDAO
}