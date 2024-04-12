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
import com.example.myevents.ui.screens.ManageEventsScreen
import com.example.myevents.ui.screens.NotificationsScreen
import com.example.myevents.ui.screens.ProfileScreen
import com.example.myevents.ui.screens.SettingsScreen
import com.example.myevents.ui.screens.WelcomeScreen

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
    data object ManageEvents : MyEventsRoute("manage", "Manage Events")
    data object Notifications : MyEventsRoute("notifications", "Notifications")
    data object Profile : MyEventsRoute("profile", "Profile")
    data object Welcome : MyEventsRoute("welcome", "Welcome")

    companion object {
        val routes = setOf(Home, EventDetails, AddEvent, Settings, ManageEvents, Notifications, Profile, Welcome)
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
                EventDetailsScreen(backStackEntry.arguments?.getString("eventId") ?: "")
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
        with(MyEventsRoute.ManageEvents) {
            composable(route) {
                ManageEventsScreen()
            }
        }
        with(MyEventsRoute.Notifications) {
            composable(route) {
                NotificationsScreen()
            }
        }
        with(MyEventsRoute.Profile) {
            composable(route) {
                ProfileScreen()
            }
        }
        with(MyEventsRoute.Welcome) {
            composable(route) {
                WelcomeScreen()
            }
        }
    }
}
