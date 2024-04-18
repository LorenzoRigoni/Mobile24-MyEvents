package com.example.myevents

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.myevents.data.database.MyEventsDatabase
import com.example.myevents.data.remote.OSMDataSource
import com.example.myevents.data.repositories.MyEventsRepository
import com.example.myevents.data.repositories.SettingsRepository
import com.example.myevents.data.repositories.UserRepository
import com.example.myevents.ui.EventsViewModel
import com.example.myevents.ui.screens.addEvent.AddEventViewModel
import com.example.myevents.ui.screens.settings.SettingsViewModel
import com.example.myevents.ui.UserViewModel
import com.example.myevents.utils.LocationService
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val Context.dataStore by preferencesDataStore("settings")

val appModule = module {
    single { get<Context>().dataStore }

    single { SettingsRepository(get()) }

    single { UserRepository(get()) }

    single { OSMDataSource(get()) }

    single { LocationService(get()) }

    single {
        Room.databaseBuilder(
            get(),
            MyEventsDatabase::class.java,
            "My-Events"
        ).fallbackToDestructiveMigration().build()
    }

    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
    }

    single {
        MyEventsRepository(
            get<MyEventsDatabase>().userDAO(),
            get<MyEventsDatabase>().eventDAO(),
            get<MyEventsDatabase>().notificationDAO()
        )
    }
    viewModel { EventsViewModel(get()) }

    viewModel { AddEventViewModel() }

    viewModel { SettingsViewModel(get()) }

    viewModel { UserViewModel(get()) }
}
