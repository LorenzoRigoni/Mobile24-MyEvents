package com.example.myevents.ui.screens.notifications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myevents.R
import com.example.myevents.data.database.Notification
import com.example.myevents.ui.EventsViewModel
import com.example.myevents.ui.NotificationsState

@Composable
fun NotificationsScreen(
    navController: NavHostController,
    eventsVm: EventsViewModel,
    notificationsState: NotificationsState
)  {
    Scaffold { contentPadding ->
        if (notificationsState.notifications.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(8.dp, 8.dp, 8.dp, 8.dp),
                modifier = Modifier.padding(contentPadding)
            ) {
                items(notificationsState.notifications) { notification ->
                    NotificationItem(notification)
                }
            }
        } else {
            NoNotificationsPlaceHolder()
        }
    }
}

@Composable
fun NotificationItem(
    item: Notification,
) {
    val split = item.notificationText.split(";")
    val notificationSubject = split[0]
    val notificationAction = split[1]

    Card(
        modifier = Modifier
            .size(150.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                when(notificationAction) {
                    "changeName" -> "${stringResource(R.string.changed_name)} $notificationSubject"
                    "changeSurname" -> "${stringResource(R.string.changed_surname)} $notificationSubject"
                    "delete" -> "$notificationSubject ${stringResource(R.string.event_deleted)}"
                    else -> "$notificationSubject ${stringResource(R.string.new_event)}"
                },
                textAlign = TextAlign.Left
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                item.date,
                textAlign = TextAlign.Left
            )
        }
    }
}

@Composable
fun NoNotificationsPlaceHolder() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            stringResource(R.string.no_notifications),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}