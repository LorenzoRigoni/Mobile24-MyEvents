package com.example.myevents.ui.screens.eventdetails

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.ModeEdit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.myevents.R
import com.example.myevents.data.database.Event
import org.osmdroid.views.MapView
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun EventDetailsScreen(
    event: Event,
    eventDetailsVm: EventDetailsViewModel
    ) {
    val openDialog = remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primary,
                onClick = {
                    openDialog.value = true
                }
            ) {
                Icon(Icons.Outlined.ModeEdit, "Edit event")
            }
        },
    ) { contentPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(contentPadding)
                .padding(12.dp)
                .fillMaxSize()
        ) {
            DrawEventInfo(
                eventDetailsVm,
                event
            )

            if (openDialog.value) {
                Dialog(
                    onDismissRequest = { openDialog.value = false }
                ) {
                    Card (
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Spacer(Modifier.size(8.dp))

                        DrawModifiableEventInfo(
                            eventDetailsVm,
                            event,
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                            )
                        )

                        Spacer(Modifier.size(8.dp))

                        Row (
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            TextButton(
                                onClick = {
                                    eventDetailsVm.clearEditState()
                                    openDialog.value = false
                                },
                                modifier = Modifier.padding(8.dp),
                            ) {
                                Text(stringResource(R.string.cancel))
                            }
                            TextButton(
                                onClick = {
                                    eventDetailsVm.editEvent(event)
                                    openDialog.value = false
                                },
                                modifier = Modifier.padding(8.dp),
                            ) {
                                Text(stringResource(R.string.save))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DrawEventInfo(eventDetailsVm: EventDetailsViewModel, event: Event) {
    val imageUri = Uri.parse(event.imageUri)
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
    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text (
            event.title,
        )
    }
    Spacer(Modifier.size(24.dp))
    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text (
            event.date,
        )
    }
    Spacer(Modifier.size(24.dp))
    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text (
            event.eventType,
        )
    }
    Spacer(Modifier.size(24.dp))
    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            if (event.isFavourite) Icons.Default.Star else Icons.Default.StarBorder,
            contentDescription = "Event star icon",
            modifier = Modifier.clickable {
                eventDetailsVm.updateIsFavourite(!event.isFavourite, event.eventID)
            }
        )
    }
    Spacer(Modifier.size(150.dp))
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        OsmMapView(
            eventDetailsVm,
            event
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawModifiableEventInfo(eventDetailsVm: EventDetailsViewModel, event: Event, colors: CardColors) {
    var editableTitle by rememberSaveable { mutableStateOf(event.title) }
    var editableType by rememberSaveable { mutableStateOf(event.eventType) }
    var editableDate by rememberSaveable { mutableStateOf(event.date) }
    var openDialog = remember { mutableStateOf(false) }
    //Capire se fare anche la mappa e l'immagine

    Card (
        colors = colors
    ) {
        Column (
            horizontalAlignment = Alignment.Start
        ) {
            Text (
                stringResource(R.string.title_event),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                eventDetailsVm.eventEditState.newTitle,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.bodyLarge
            )
            Divider(
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 25.dp)
            )
            OutlinedTextField(
                value = editableTitle,
                onValueChange = {
                    editableTitle = it
                    eventDetailsVm.eventEditState.newTitle = it
                },
                label = { Text("Title") },
                textStyle = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )
            Divider(
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 25.dp)
            )
            Text (
                stringResource(R.string.type_event),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                eventDetailsVm.eventEditState.newType,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.bodyLarge
            )
            Divider(
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 25.dp)
            )
            OutlinedTextField(
                value = editableType,
                onValueChange = {
                    editableType = it
                    eventDetailsVm.eventEditState.newType = it
                },
                label = { Text("Event type") },
                textStyle = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )
            Divider(
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 25.dp)
            )
            Text (
                stringResource(R.string.date_event),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                eventDetailsVm.eventEditState.newDate,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.bodyLarge
            )
            Divider(
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 25.dp)
            )
            Button(
                onClick = { openDialog.value = true }
            ) {
                Text(stringResource(R.string.select_new_date))
            }
            if (openDialog.value) {
                val datePickerState = rememberDatePickerState()
                val confirmEnabled = remember { derivedStateOf { datePickerState.selectedDateMillis != null }}
                DatePickerDialog(
                    onDismissRequest = { openDialog.value = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                openDialog.value = false
                                editableDate = datePickerState.selectedDateMillis?.let { millis ->
                                    val selectedDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                                    selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                } ?: ""
                                eventDetailsVm.eventEditState.newDate = editableDate
                            },
                            enabled = confirmEnabled.value
                        ) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                openDialog.value = false
                            }
                        ) {
                            Text(stringResource(R.string.cancel))
                        }
                    }) {
                    DatePicker(state = datePickerState)
                }
            }
        }
    }
}

@Composable
fun OsmMapView(eventDetailsVm: EventDetailsViewModel, event: Event) {
    AndroidView(
        factory = {context ->
            MapView(context).apply {
                eventDetailsVm.openMap(event.latitude, event.longitude, this, context)
            }
        }
    )
}