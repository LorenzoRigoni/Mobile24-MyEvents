package com.example.myevents.ui.screens.home

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.ModeEdit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.myevents.data.database.Event
import com.example.myevents.ui.EventsState
import com.example.myevents.ui.EventsViewModel
import com.example.myevents.ui.FilterEnum
import com.example.myevents.ui.MyEventsRoute

@Composable
fun HomeScreen(
    eventsVm: EventsViewModel,
    state: EventsState,
    navController: NavHostController
) {
    Scaffold(
        floatingActionButton = {
            Row (
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                FloatingActionButton(
                    containerColor = MaterialTheme.colorScheme.primary,
                    onClick = { navController.navigate(MyEventsRoute.ManageEvents.route) }
                ) {
                    Icon(Icons.Outlined.ModeEdit, "Manage events")
                }
                FloatingActionButton(
                    containerColor = MaterialTheme.colorScheme.primary,
                    onClick = { navController.navigate(MyEventsRoute.AddEvent.route) }
                ) {
                    Icon(Icons.Outlined.Add, stringResource(R.string.add_event))
                }
            }
        },
    ) { contentPadding ->
        Column {
            FilterChips(
                eventsVm,
            )
            if (state.events.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(8.dp, 8.dp, 8.dp, 8.dp),
                    modifier = Modifier.padding(contentPadding)
                ) {
                    items(state.events) { item ->
                        EventItem(
                            item,
                            onClick = {
                                navController.navigate(MyEventsRoute.EventDetails.buildRoute(item.eventID.toString()))
                            },
                            eventsVm
                        )
                    }
                }
            } else {
                NoEventsPlaceHolder()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventItem(
    item: Event,
    onClick: () -> Unit,
    eventsVm: EventsViewModel
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
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
            Column {
                Text(
                    item.title,
                    textAlign = TextAlign.Left
                )
                Text(
                    item.eventType,
                    textAlign = TextAlign.Left
                )
            }
            Spacer(modifier = Modifier.size(8.dp))
            Column {
                Icon(
                    if (item.isFavourite) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = "Event star icon",
                    modifier = Modifier.clickable {
                        eventsVm.updateIsFavourite(!item.isFavourite, item.eventID)
                    }.align(Alignment.End)
                )
                Text(
                    item.date,
                )
            }
        }
    }
}

@Composable
fun NoEventsPlaceHolder() {
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
        Text(
            stringResource(R.string.tap_below),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChips(
    eventsVm: EventsViewModel,
) {
    var selectedFuture by remember { mutableStateOf(eventsVm.filter.value == FilterEnum.SHOW_FUTURE_EVENTS)}
    var selectedAll by remember { mutableStateOf(eventsVm.filter.value == FilterEnum.SHOW_ALL_EVENTS) }
    var selectedPast by remember { mutableStateOf(eventsVm.filter.value == FilterEnum.SHOW_PAST_EVENTS) }
    var selectedFavourites by remember { mutableStateOf(eventsVm.filter.value == FilterEnum.SHOW_FAVOURITES_EVENTS) }
    Column (
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            FilterChip(
                selected = selectedFuture,
                onClick = {
                    if (!selectedFuture) {
                        selectedFuture = true
                        selectedAll = false
                        selectedPast = false
                        selectedFavourites = false
                        eventsVm.filter.value = FilterEnum.SHOW_FUTURE_EVENTS
                        eventsVm.updateEvents(FilterEnum.SHOW_FUTURE_EVENTS)
                    }
                },
                label = {
                    Text(stringResource(R.string.future_events))
                },
                leadingIcon = if (selectedFuture) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Done icon",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else {
                    null
                }
            )
            FilterChip(
                selected = selectedAll,
                onClick = {
                    if (!selectedAll) {
                        selectedAll = true
                        selectedFuture = false
                        selectedPast = false
                        selectedFavourites = false
                        eventsVm.filter.value = FilterEnum.SHOW_ALL_EVENTS
                        eventsVm.updateEvents(FilterEnum.SHOW_ALL_EVENTS)
                    }
                },
                label = {
                    Text(stringResource(R.string.all_events))
                },
                leadingIcon = if (selectedAll) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Done icon",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else {
                    null
                }
            )
            FilterChip(
                selected = selectedPast,
                onClick = {
                    if (!selectedPast) {
                        selectedPast = true
                        selectedFuture = false
                        selectedAll = false
                        selectedFavourites = false
                        eventsVm.filter.value = FilterEnum.SHOW_PAST_EVENTS
                        eventsVm.updateEvents(FilterEnum.SHOW_PAST_EVENTS)
                    }
                },
                label = {
                    Text(stringResource(R.string.past_events))
                },
                leadingIcon = if (selectedPast) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Done icon",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else {
                    null
                }
            )
            FilterChip(
                selected = selectedFavourites,
                onClick = {
                    if (!selectedFavourites) {
                        selectedFavourites = true
                        selectedFuture = false
                        selectedPast = false
                        selectedAll = false
                        eventsVm.filter.value = FilterEnum.SHOW_FAVOURITES_EVENTS
                        eventsVm.updateEvents(FilterEnum.SHOW_FAVOURITES_EVENTS)
                    }
                },
                label = {
                    Text(stringResource(R.string.favourites_events))
                },
                leadingIcon = if (selectedFavourites) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Done icon",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else {
                    null
                }
            )
        }
    }
}