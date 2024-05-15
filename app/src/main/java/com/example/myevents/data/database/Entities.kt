package com.example.myevents.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class User(
    @PrimaryKey val username: String,
    @ColumnInfo val name: String,
    @ColumnInfo val surname: String,
    @ColumnInfo val password: String,
    @ColumnInfo val imageUri: String?
)

@Entity
data class Event(
    @PrimaryKey(autoGenerate = true)
    val eventID: Int = 0,
    @ColumnInfo val username: String,
    @ColumnInfo val eventType: String,
    @ColumnInfo val title: String,
    @ColumnInfo val longitude: String,
    @ColumnInfo val latitude: String,
    @ColumnInfo val date: String,
    @ColumnInfo val isFavourite: Boolean,
    @ColumnInfo val imageUri: String?
)

@Entity
data class Notification(
    @PrimaryKey(autoGenerate = true)
    val notificationID: Int,
    @ColumnInfo val username: String,
    @ColumnInfo val notificationText: String,
)