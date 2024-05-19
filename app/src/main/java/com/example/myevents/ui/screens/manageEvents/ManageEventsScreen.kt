package com.example.myevents.ui.screens.manageEvents

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.SelectAll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.myevents.R
import com.example.myevents.ui.EventsState
import com.example.myevents.ui.EventsViewModel

@Composable
fun ManageEventsScreen(
    eventsVm: EventsViewModel,
    state: EventsState,
    navController: NavHostController
) {
    val switchStates = remember { mutableStateMapOf<Int, Boolean>().apply {
        putAll(state.events.map { it.eventID }.associateWith { false })
    }}

    Scaffold (
        floatingActionButton = {
            if (state.events.isNotEmpty()) {
                FloatingActionButton(
                    containerColor = MaterialTheme.colorScheme.primary,
                    onClick = {
                        if (switchStates.values.contains(false)) {
                            switchStates.forEach { (key, _) ->
                                switchStates[key] = true
                                eventsVm.eventsToDelete.add(key)
                            }
                        } else {
                            switchStates.forEach { (key, _) ->
                                switchStates[key] = false
                                eventsVm.eventsToDelete.remove(key)
                            }
                        }
                    }
                ) {
                    Icon(Icons.Outlined.SelectAll, "Select all")
                }
            }
        },
    ) { contentPadding ->
        if (state.events.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(8.dp, 8.dp, 8.dp, 8.dp),
                modifier = Modifier.padding(contentPadding)
            ) {
                items(state.events) { item ->
                    Card(
                        modifier = Modifier
                            .size(150.dp)
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Row (
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(start = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val imageUri = Uri.parse(item.imageUri)
                            if (imageUri.path?.isNotEmpty() == true) {
                                AsyncImage(
                                    ImageRequest.Builder(LocalContext.current)
                                        .data(imageUri)
                                        .crossfade(true)
                                        .build(),
                                    stringResource(R.string.event_pic),
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .size(72.dp)
                                )
                            } else {
                                Image(
                                    Icons.Outlined.Image,
                                    stringResource(R.string.event_pic),
                                    contentScale = ContentScale.Fit,
                                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondary),
                                    modifier = Modifier
                                        .size(72.dp)
                                        .background(MaterialTheme.colorScheme.secondary)
                                        .padding(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                item.title,
                                textAlign = TextAlign.Left
                            )
                            Switch(
                                checked = switchStates[item.eventID] ?: false,
                                onCheckedChange = { isChecked ->
                                    switchStates[item.eventID] = isChecked
                                    if (isChecked) {
                                        eventsVm.eventsToDelete.add(item.eventID)
                                    } else {
                                        eventsVm.eventsToDelete.remove(item.eventID)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        } else {
            NoEventsPlaceHolder(Modifier.padding(contentPadding))
        }
    }
}

@Composable
fun NoEventsPlaceHolder(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            stringResource(R.string.no_events),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }
}