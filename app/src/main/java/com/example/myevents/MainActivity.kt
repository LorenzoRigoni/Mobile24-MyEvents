package com.example.myevents

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myevents.ui.EventsViewModel
import com.example.myevents.ui.MyEventsNavGraph
import com.example.myevents.ui.MyEventsRoute
import com.example.myevents.ui.UserViewModel
import com.example.myevents.ui.composables.AppBar
import com.example.myevents.ui.screens.addEvent.AddEventViewModel
import com.example.myevents.ui.screens.eventdetails.EventDetailsViewModel
import com.example.myevents.ui.screens.settings.SettingsViewModel
import com.example.myevents.ui.theme.MyEventsTheme
import com.example.myevents.utils.LocationService
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel

@SuppressLint("StaticFieldLeak")
private lateinit var locationService: LocationService

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationService = LocationService(this)

        setContent {
            MyEventsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val backStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute by remember {
                        derivedStateOf {
                            MyEventsRoute.routes.find {
                                it.route == backStackEntry?.destination?.route
                            } ?: MyEventsRoute.Welcome
                        }
                    }
                    val userVm = koinViewModel<UserViewModel>()

                    val eventsVm = koinViewModel<EventsViewModel>()
                    val eventsState by eventsVm.state.collectAsStateWithLifecycle()

                    val addEventVm = koinViewModel<AddEventViewModel>()
                    val eventDetailsVm = koinViewModel<EventDetailsViewModel>()
                    val settingsVm = koinViewModel<SettingsViewModel>()

                    val theme = settingsVm.getTheme();

                    Scaffold(
                        topBar = {
                            AppBar(
                                navController,
                                currentRoute,
                                userVm,
                                eventsVm,
                                addEventVm,
                                eventDetailsVm,
                                settingsVm)
                        }
                    ) { contentPadding ->
                        MyEventsNavGraph(
                            navController,
                            userVm,
                            eventsVm,
                            eventsState,
                            addEventVm,
                            eventDetailsVm,
                            settingsVm,
                            modifier =  Modifier.padding(contentPadding)
                        )
                    }
                }
            }
        }
    }
}

fun getLocationService(): LocationService {
    return locationService
}
