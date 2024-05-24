package com.example.myevents.ui.composables

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Badge
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myevents.R
import com.example.myevents.ui.EventsViewModel
import com.example.myevents.ui.MyEventsRoute
import com.example.myevents.ui.UserViewModel
import com.example.myevents.ui.screens.addEvent.AddEventViewModel
import com.example.myevents.ui.screens.eventdetails.EventDetailsViewModel
import com.example.myevents.ui.screens.settings.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    navController: NavHostController,
    currentRoute: MyEventsRoute,
    userVm: UserViewModel,
    eventsVm: EventsViewModel,
    addEventVm: AddEventViewModel,
    eventDetailsVm: EventDetailsViewModel,
    settingsVm: SettingsViewModel
) {
    var showMenu by remember { mutableStateOf(false) }
    val onMenuClicked: () -> Unit = { showMenu = !showMenu }
    val onMenuDismissed: () -> Unit = { showMenu = false }
    val badgeCount by eventsVm.notificationBadges.collectAsState()
    val ctx = LocalContext.current

    CenterAlignedTopAppBar(
        title = {
            Text(
                when (currentRoute) {
                    MyEventsRoute.Welcome -> stringResource(R.string.welcomeScreenTitle)
                    MyEventsRoute.Home -> stringResource(R.string.homeScreenTitle)
                    MyEventsRoute.AddEvent -> stringResource(R.string.addEventScreenTitle)
                    MyEventsRoute.EventDetails -> stringResource(R.string.eventDetailScreenTitle)
                    MyEventsRoute.ManageEvents -> stringResource(R.string.manageEventsScreenTitle)
                    MyEventsRoute.Notifications -> stringResource(R.string.notificationsScreenTitle)
                    MyEventsRoute.Profile -> stringResource(R.string.profileScreenTitle)
                    MyEventsRoute.Login -> stringResource(R.string.loginScreenTitle)
                    MyEventsRoute.Register -> stringResource(R.string.registerScreenTitle)
                    MyEventsRoute.Settings -> stringResource(R.string.settingsScreenTitle)
                    else -> stringResource(R.string.app_name)
                },
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
                    AddNotificationsButton(
                        navController,
                        eventsVm::resetNotificationBadges,
                        badgeCount
                    )
                    AddDropDownMenu(
                        navController,
                        showMenu,
                        onMenuClicked,
                        onMenuDismissed,
                        userVm::logout
                    )
                }
                MyEventsRoute.AddEvent -> {
                    AddConfirmButton(
                        navController,
                        addEventVm::addEvent,
                        eventsVm::incrementNotificationBadge,
                        addEventVm::checkCanAdd,
                        ctx
                    )
                }
                MyEventsRoute.EventDetails -> {
                    AddDeleteButton(
                        navController,
                        eventDetailsVm::deleteSingleEvent,
                        eventsVm::incrementNotificationBadge,
                        MyEventsRoute.Home.route,
                    )
                }
                MyEventsRoute.ManageEvents -> {
                    AddDeleteButton(
                        navController,
                        eventsVm::deleteEventsFromListOfIds,
                        {},
                        MyEventsRoute.ManageEvents.route,
                    )
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
            text = { Text(stringResource(R.string.settings)) },
            trailingIcon = { Icon(Icons.Outlined.Settings, "Settings") },
            onClick = { navController.navigate(MyEventsRoute.Settings.route) }
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.profile)) },
            trailingIcon = { Icon(Icons.Outlined.Person, "Profile") },
            onClick = { navController.navigate(MyEventsRoute.Profile.route) }
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.logout)) },
            trailingIcon = { Icon(Icons.Outlined.Logout, "Logout") },
            onClick = {
                logoutAction()
                navController.navigate(MyEventsRoute.Welcome.route)
            }
        )
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddNotificationsButton (
    navController: NavHostController,
    resetNotificationBadge: () -> Unit,
    badgeCount: Int
) {
    Box {
        IconButton(onClick = {
            navController.navigate(MyEventsRoute.Notifications.route)
            resetNotificationBadge()
        }) {
            Icon(Icons.Outlined.Notifications, "Notifications")
        }
        if (badgeCount > 0) {
            Badge(
                modifier = Modifier.align(Alignment.Center)
                    .padding(start = 20.dp, bottom = 20.dp),
                content = { Text("$badgeCount") }
            )
        }
    }
}

@Composable
private fun AddConfirmButton (
    navController: NavHostController,
    saveAction: () -> Unit,
    incrementNotificationBadge: () -> Unit,
    check: () -> Boolean,
    context: Context
) {
    IconButton(onClick = {
        if (check()) {
            saveAction()
            incrementNotificationBadge()
            navController.navigate(MyEventsRoute.Home.route)
        } else {
            Toast.makeText(context, "You have to complete all the fields", Toast.LENGTH_SHORT).show()
        }
    }) {
        Icon(Icons.Outlined.Check, "Cancel")
    }
}
@Composable
private fun AddDeleteButton (
    navController: NavHostController,
    deleteAction: () -> Unit,
    incrementNotificationBadge: () -> Unit,
    returnRoute: String,
) {
    IconButton(onClick = {
        incrementNotificationBadge()
        navController.navigate(returnRoute)
        deleteAction()
    }) {
        Icon(Icons.Outlined.Delete, "Delete")
    }
}