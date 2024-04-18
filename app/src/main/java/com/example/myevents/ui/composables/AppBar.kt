package com.example.myevents.ui.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import com.example.myevents.ui.MyEventsRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    navController: NavHostController,
    currentRoute: MyEventsRoute
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                currentRoute.title,
                fontWeight = FontWeight.Medium,
            )
        },
        navigationIcon = {
            if (navController.previousBackStackEntry != null &&
                currentRoute != MyEventsRoute.AddEvent) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBack,
                        contentDescription = "Back button"
                    )
                }
            }
            if (currentRoute == MyEventsRoute.AddEvent) {
                AddCancelButton(navController)
            }
        },
        actions = {
            when (currentRoute) {
                MyEventsRoute.Welcome -> {
                    AddSettingsButton(navController)
                }
                MyEventsRoute.Home -> {
                    AddSearchButton()
                    AddNotificationsButton(navController)
                    AddProfileButton(navController)
                    AddSettingsButton(navController)
                }
                MyEventsRoute.AddEvent -> {
                    //Add confirm and cancel button
                    //something like V and X (check and cancel)
                    //Like -> |X       Add event       V|
                    //when u press X it goes back to home, when u press V it adds the event
                    AddConfirmButton(navController)
                }
                MyEventsRoute.EventDetails -> {
                    AddConfirmButton(navController)
                }
                MyEventsRoute.ManageEvents -> {
                    AddDeleteButton(navController)
                }
                MyEventsRoute.Notifications -> {
                    AddSettingsButton(navController)
                }
                MyEventsRoute.Profile -> {
                    AddConfirmButton(navController)
                }
                MyEventsRoute.Login -> {}
                MyEventsRoute.Register -> {}
                MyEventsRoute.Settings -> {}
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    )
}

@Composable
private fun AddSettingsButton (navController: NavHostController) {
    IconButton(onClick = { navController.navigate(MyEventsRoute.Settings.route) }) {
        Icon(Icons.Outlined.Settings, "Settings")
    }
}
@Composable
private fun AddSearchButton () {
    IconButton(onClick = { /*TODO*/ }) {
        Icon(Icons.Outlined.Search, "Search")
    }
}
@Composable
private fun AddNotificationsButton (navController: NavHostController) {
    IconButton(onClick = { navController.navigate(MyEventsRoute.Notifications.route) }) {
        Icon(Icons.Outlined.Notifications, "Notifications")
    }
}
@Composable
private fun AddProfileButton (navController: NavHostController) {
    IconButton(onClick = { navController.navigate(MyEventsRoute.Profile.route) }) {
        Icon(Icons.Outlined.Person, "Profile")
    }
}
@Composable
private fun AddConfirmButton (navController: NavHostController) {
    IconButton(onClick = {
        //ADD HERE LOGIC FOR SAVING INTO DB
        navController.navigate(MyEventsRoute.Home.route)
    }) {
        Icon(Icons.Outlined.Check, "Cancel")
    }
}
@Composable
private fun AddCancelButton (navController: NavHostController) {
    IconButton(onClick = { navController.navigate(MyEventsRoute.Home.route) }) {
        Icon(Icons.Outlined.Close, "Confirm")
    }
}
@Composable
private fun AddDeleteButton (navController: NavHostController) {
    IconButton(onClick = {
        //Delete selected events
        navController.navigate(MyEventsRoute.ManageEvents.route) {
            popUpTo(MyEventsRoute.ManageEvents.route) { inclusive = true }
        }
    }) {
        Icon(Icons.Outlined.Check, "Delete")
    }
}