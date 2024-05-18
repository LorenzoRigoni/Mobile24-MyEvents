package com.example.myevents.ui.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import com.example.myevents.ui.EventsViewModel
import com.example.myevents.ui.MyEventsRoute
import com.example.myevents.ui.UserViewModel
import com.example.myevents.ui.screens.addEvent.AddEventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    navController: NavHostController,
    currentRoute: MyEventsRoute,
    userVm: UserViewModel,
    eventsVm: EventsViewModel,
    addEventVm: AddEventViewModel,
) {
    var showMenu by remember { mutableStateOf(false) }
    val onMenuClicked: () -> Unit = { showMenu = !showMenu }
    val onMenuDismissed: () -> Unit = { showMenu = false }

    CenterAlignedTopAppBar(
        title = {
            Text(
                currentRoute.title,
                fontWeight = FontWeight.Medium,
            )
        },
        navigationIcon = {
            if (currentRoute != MyEventsRoute.Welcome &&
                currentRoute != MyEventsRoute.Login &&
                currentRoute != MyEventsRoute.Register) {
                IconButton(onClick = { navController.navigate(MyEventsRoute.Home.route) }) {
                    Icon(
                        if (currentRoute != MyEventsRoute.AddEvent) Icons.Outlined.Home else Icons.Outlined.Close,
                        "Back button"
                    )
                }
            }
        },
        actions = {
            when (currentRoute) {
                MyEventsRoute.Welcome -> {
                    AddLogoutButton(navController, userVm::logout)
                }
                MyEventsRoute.Home -> {
                    AddFilterButton()
                    AddNotificationsButton(navController)
                    AddDropDownMenu(
                        navController,
                        showMenu,
                        onMenuClicked,
                        onMenuDismissed,
                        userVm::logout
                    )
                }
                MyEventsRoute.AddEvent -> {
                    AddConfirmButton(navController, addEventVm::addEvent, addEventVm::checkCanAdd)
                }
                MyEventsRoute.EventDetails -> {}
                MyEventsRoute.ManageEvents -> {
                    AddDeleteButton(navController)
                }
                MyEventsRoute.Notifications -> {
                    AddDropDownMenu(
                        navController,
                        showMenu,
                        onMenuClicked,
                        onMenuDismissed,
                        userVm::logout
                    )
                }
                MyEventsRoute.Profile -> {}
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
private fun AddDropDownMenu (
    navController: NavHostController,
    showMenu: Boolean,
    onMenuClicked: () -> Unit,
    onMenuDismissed: () -> Unit,
    logoutAction: () -> Unit
) {
    IconButton(onClick = onMenuClicked) {
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "Menu"
        )
    }
    DropdownMenu(
        expanded = showMenu,
        onDismissRequest = onMenuDismissed
    ) {
        DropdownMenuItem(
            text = { Text("Settings") },
            trailingIcon = { Icon(Icons.Outlined.Settings, "Settings") },
            onClick = { navController.navigate(MyEventsRoute.Settings.route) }
        )
        DropdownMenuItem(
            text = { Text("Profile") },
            trailingIcon = { Icon(Icons.Outlined.Person, "Profile") },
            onClick = { navController.navigate(MyEventsRoute.Profile.route) }
        )
        DropdownMenuItem(
            text = { Text("Logout") },
            trailingIcon = { Icon(Icons.Outlined.Logout, "Logout") },
            onClick = {
                logoutAction()
                navController.navigate(MyEventsRoute.Welcome.route)
            }
        )
    }
}
@Composable
private fun AddFilterButton () {
    IconButton(onClick = { /*TODO*/ }) {
        Icon(Icons.Outlined.FilterList, "Filter")
    }
}
@Composable
private fun AddLogoutButton (navController: NavHostController, logoutAction: () -> Unit) {
    IconButton(onClick = {
        logoutAction()
        navController.navigate(MyEventsRoute.Welcome.route)
    }) {
        Icon(Icons.Outlined.Logout, "Logout")
    }
}
@Composable
private fun AddNotificationsButton (navController: NavHostController) {
    IconButton(onClick = { navController.navigate(MyEventsRoute.Notifications.route) }) {
        Icon(Icons.Outlined.Notifications, "Notifications")
    }
}
@Composable
private fun AddConfirmButton (navController: NavHostController, saveAction: () -> Unit, check: () -> Boolean) {
    IconButton(onClick = {
        if (check()) {
            saveAction()
            navController.navigate(MyEventsRoute.Home.route)
        } else {
            // TODO TOAST COME NEL LOGIN
        }
    }) {
        Icon(Icons.Outlined.Check, "Cancel")
    }
}
@Composable
private fun AddDeleteButton (navController: NavHostController) {
    IconButton(onClick = {
        navController.navigate(MyEventsRoute.ManageEvents.route)
    }) {
        Icon(Icons.Outlined.Delete, "Delete")
    }
}