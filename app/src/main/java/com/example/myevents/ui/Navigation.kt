package com.example.myevents.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.myevents.ui.screens.AddEventScreen
import com.example.myevents.ui.screens.EventDetailsScreen
import com.example.myevents.ui.screens.HomeScreen
import com.example.myevents.ui.screens.SettingsScreen

sealed class MyEventsRoute(
    val route: String,
    val title: String,
    val arguments: List<NamedNavArgument> = emptyList()
) {
    data object Home : MyEventsRoute("events", "MyEvents")
    data object EventDetails : MyEventsRoute(
        "events/{eventId}",
        "Event Details",
        listOf(navArgument("eventId") { type = NavType.StringType })
    ) {
        fun buildRoute(eventId: String) = "events/$eventId"
    }
    data object AddEvent : MyEventsRoute("events/add", "Add Event")
    data object Settings : MyEventsRoute("settings", "Settings")

    companion object {
        val routes = setOf(Home, EventDetails, AddEvent, Settings)
    }
}

@Composable
fun MyEventsNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = MyEventsRoute.Home.route,
        modifier = modifier
    ) {
        with(MyEventsRoute.Home) {
            composable(route) {
                HomeScreen(navController)
            }
        }
        with(MyEventsRoute.EventDetails) {
            composable(route, arguments) { backStackEntry ->
                EventDetailsScreen(backStackEntry.arguments?.getString("travelId") ?: "")
            }
        }
        with(MyEventsRoute.AddEvent) {
            composable(route) {
                AddEventScreen(navController)
            }
        }
        with(MyEventsRoute.Settings) {
            composable(route) {
                SettingsScreen()
            }
        }
    }
}
