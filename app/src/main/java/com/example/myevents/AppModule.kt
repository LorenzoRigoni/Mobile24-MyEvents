package com.example.myevents

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.myevents.data.database.MyEventsDatabase
import com.example.myevents.data.repositories.MyEventsRepository
import com.example.myevents.data.repositories.SettingsRepository
import com.example.myevents.ui.screens.addEvent.AddEventViewModel
import com.example.myevents.ui.screens.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val Context.dataStore by preferencesDataStore("settings")

val appModule = module {
    single { get<Context>().dataStore }

    single { SettingsRepository(get()) }

    single {
        Room.databaseBuilder(
            get(),
            MyEventsDatabase::class.java,
            "My-Events"
        ).build()
    }

    single {
        MyEventsRepository(
            get<MyEventsDatabase>().userDAO(),
            get<MyEventsDatabase>().eventDAO(),
            get<MyEventsDatabase>().notificationDAO()
        )
    }

    viewModel { AddEventViewModel() }

    viewModel { SettingsViewModel(get()) }
}
