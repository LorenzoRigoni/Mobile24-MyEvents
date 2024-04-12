package com.example.myevents

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.example.myevents.data.repositories.SettingsRepository
import com.example.myevents.ui.screens.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val Context.dataStore by preferencesDataStore("settings")

val appModule = module {
    single { get<Context>().dataStore }

    single { SettingsRepository(get()) }

    viewModel { AddTravelViewModel() }

    viewModel { SettingsViewModel(get()) }
}
