package com.example.myevents.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.myevents.ui.screens.addEvent.AddEventScreen
import com.example.myevents.ui.screens.addEvent.AddEventViewModel
import com.example.myevents.ui.screens.eventdetails.EventDetailsScreen
import com.example.myevents.ui.screens.home.HomeScreen
import com.example.myevents.ui.screens.manageEvents.ManageEventsScreen
import com.example.myevents.ui.screens.notifications.NotificationsScreen
import com.example.myevents.ui.screens.profile.ProfileScreen
import com.example.myevents.ui.screens.settings.SettingsScreen
import com.example.myevents.ui.screens.settings.SettingsViewModel
import com.example.myevents.ui.screens.user.LoginScreen
import com.example.myevents.ui.screens.user.RegisterScreen
import com.example.myevents.ui.screens.welcome.WelcomeScreen
import org.koin.androidx.compose.koinViewModel

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
    data object Login : MyEventsRoute("login", "Login")
    data object Register : MyEventsRoute("register", "Register")

    companion object {
        val routes = setOf(
            Home,
            EventDetails,
            AddEvent, Settings,
            ManageEvents,
            Notifications,
            Profile,
            Welcome,
            Login,
            Register
        )
    }
}

@Composable
fun MyEventsNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val eventsVm = koinViewModel<EventsViewModel>()
    val eventsState by eventsVm.state.collectAsStateWithLifecycle()

    val userVm = koinViewModel<UserViewModel>()

    NavHost(
        navController = navController,
        startDestination = MyEventsRoute.Welcome.route,
        modifier = modifier
    ) {
        with(MyEventsRoute.Welcome) {
            composable(route) {
                WelcomeScreen(userVm.state, navController, userVm::logout)
            }
        }
        with(MyEventsRoute.Home) {
            composable(route) {
                HomeScreen(navController)
            }
        }
        with(MyEventsRoute.EventDetails) {
            composable(route, arguments) { backStackEntry ->
                val event = requireNotNull(eventsState.events.find {
                    it.eventID == backStackEntry.arguments?.getString("eventId")?.toInt()
                })
                EventDetailsScreen(event)
            }
        }
        with(MyEventsRoute.AddEvent) {
            composable(route) {
                val addEventVm = koinViewModel<AddEventViewModel>()
                val state by addEventVm.state.collectAsStateWithLifecycle()
                AddEventScreen(
                    state = state,
                    actions = addEventVm.actions,
                    onSubmit = { eventsVm.addEvent(state.toEvent()) },
                    navController = navController
                )
            }
        }
        with(MyEventsRoute.Settings) {
            composable(route) {
                val settingsVm = koinViewModel<SettingsViewModel>()
                SettingsScreen(settingsVm.state, settingsVm::setUsername)
            }
        }
        with(MyEventsRoute.ManageEvents) {
            composable(route) {
                ManageEventsScreen(navController)
            }
        }
        with(MyEventsRoute.Notifications) {
            composable(route) {
                NotificationsScreen(navController)
            }
        }
        with(MyEventsRoute.Profile) {
            composable(route) {
                ProfileScreen(navController)
            }
        }
        with(MyEventsRoute.Login) {
            composable(route) {
                LoginScreen(navController, userVm::setLoggedUser, userVm::checkLogin)
            }
        }
        with(MyEventsRoute.Register) {
            composable(route) {
                RegisterScreen(navController, userVm::setLoggedUser)
            }
        }
    }
}
