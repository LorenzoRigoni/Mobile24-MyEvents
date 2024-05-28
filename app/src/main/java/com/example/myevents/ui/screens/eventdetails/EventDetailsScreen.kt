package com.example.myevents.ui.screens.eventdetails

import android.app.TimePickerDialog
import android.content.Context
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.myevents.R
import com.example.myevents.data.database.Event
import com.example.myevents.utils.dateTimeFormatterFromDBstring
import com.example.myevents.utils.dateTimeFormatterFromLocalDateTime
import org.osmdroid.views.MapView
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun EventDetailsScreen(
    event: Event,
    eventDetailsVm: EventDetailsViewModel
) {
    val openDialog = remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .padding(contentPadding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Row {
                DrawEventImage(
                    imageUri = Uri.parse(event.imageUri),
                )
                Column (
                    modifier = Modifier
                        .width(LocalConfiguration.current.screenWidthDp.dp / 2)
                        .padding(end = 16.dp, top = 16.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .height(LocalConfiguration.current.screenWidthDp.dp / 11)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        Icon(
                            if (event.isFavourite) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Event star icon",
                            modifier = Modifier
                                .clickable {
                                    eventDetailsVm.updateIsFavourite(
                                        !event.isFavourite,
                                        event.eventID
                                    )
                                }
                                .scale(1.5f)
                                .padding(8.dp)
                        )
                    }
                    Column (
                        modifier = Modifier
                            .height(LocalConfiguration.current.screenWidthDp.dp / 3)
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        DrawEventInfo(event)
                    }
                }
            }
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .height(LocalConfiguration.current.screenWidthDp.dp)
            ) {
                OsmMapView(eventDetailsVm, event)
            }

            if (openDialog.value) {
                Dialog(
                    onDismissRequest = {
                        openDialog.value = false
                    }
                ) {
                    Card (
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.verticalScroll(scrollState)
                        ) {
                            DrawModifiableEventInfo(
                                eventDetailsVm,
                                event,
                                colors = CardDefaults.cardColors()
                            )

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
}

@Composable
fun DrawEventImage(imageUri: Uri) {
    Box(
        modifier = Modifier
            .width(LocalConfiguration.current.screenWidthDp.dp / 2)
            .height(LocalConfiguration.current.screenWidthDp.dp / 2)
            .padding(end = 32.dp, bottom = 32.dp, top = 16.dp, start = 16.dp),
        contentAlignment = Alignment.TopStart
    ) {
        if (imageUri.path?.isNotEmpty() == true) {
            AsyncImage(
                ImageRequest.Builder(LocalContext.current)
                    .data(imageUri)
                    .crossfade(true)
                    .build(),
                stringResource(R.string.event_pic),
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxSize()
            )
        } else {
            Image(
                Icons.Outlined.Image,
                stringResource(R.string.event_pic),
                contentScale = ContentScale.FillWidth,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondary),
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.secondary)
            )
        }
    }
}

@Composable
fun DrawEventInfo(event: Event) {
    Text(
        event.title,
        style = MaterialTheme.typography.titleLarge
    )

    Spacer(Modifier.size(8.dp))

    Text(
        event.eventType,
    )

    Spacer(Modifier.size(8.dp))

    Text(
        event.date,
    )
}

@Composable
fun DrawModifiableEventInfo(eventDetailsVm: EventDetailsViewModel, event: Event, colors: CardColors) {
    var editableTitle by rememberSaveable { mutableStateOf(event.title) }
    var editableType by rememberSaveable { mutableStateOf(event.eventType) }

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    Card (
        colors = colors,
        modifier = Modifier
            .padding(16.dp)
    ) {
        Column (
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .width(if (screenWidth > screenHeight) screenHeight / 2 else screenWidth / 2)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = editableTitle,
                onValueChange = {
                    editableTitle = it
                    eventDetailsVm.setNewTitle(it)
                },
                label = { Text(stringResource(R.string.title_event)) },
                textStyle = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = editableType,
                onValueChange = {
                    editableType = it
                    eventDetailsVm.setNewType(it)
                },
                label = { Text(stringResource(R.string.type_event)) },
                textStyle = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            DateTimePicker(
                onDateTimeSelected = {
                    eventDetailsVm.setNewDate(it.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                },
                context = LocalContext.current,
                modifier = Modifier.align(Alignment.Start),
                event
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePicker(
    onDateTimeSelected: (LocalDateTime) -> Unit,
    context: Context,
    modifier: Modifier,
    event: Event
) {
    var date by remember { mutableStateOf(LocalDate.now()) }
    var time by remember { mutableStateOf(LocalTime.now()) }
    var isDatePickerDialogShowing by remember { mutableStateOf(false) }
    var isTimePickerDialogShowing by remember { mutableStateOf(false) }

    Button(
        onClick = {
            isDatePickerDialogShowing = true
            isTimePickerDialogShowing = false
        },
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(32.dp)
    ) {
        Text(
            "${stringResource(R.string.select_new_date)}:\n${dateTimeFormatterFromDBstring(event.date)}"
        )
    }

    if (isDatePickerDialogShowing) {
        val datePickerState = rememberDatePickerState()
        val confirmEnabled = remember {
            derivedStateOf { datePickerState.selectedDateMillis != null }
        }
        DatePickerDialog(
            onDismissRequest = {
                isDatePickerDialogShowing = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        date = Instant.ofEpochMilli(datePickerState.selectedDateMillis!!)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        isDatePickerDialogShowing = false
                        isTimePickerDialogShowing = true
                    },
                    enabled = confirmEnabled.value
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        isDatePickerDialogShowing = false
                    }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (isTimePickerDialogShowing) {
        TimePickerDialog(
            context,
            { _, selectedHour: Int, selectedMinute: Int ->
                time = LocalTime.of(selectedHour, selectedMinute)
                onDateTimeSelected(LocalDateTime.of(date, time))
                isTimePickerDialogShowing = false
            },
            time.hour,
            time.minute,
            true
        ).show()
    }
}

@Composable
fun OsmMapView(eventDetailsVm: EventDetailsViewModel, event: Event) {
    AndroidView(
        factory = {context ->
            MapView(context).apply {
                eventDetailsVm.openMap(event.latitude, event.longitude, this, context)
            }
        },
        modifier = Modifier
            .graphicsLayer {
                clip = true
                shape = RoundedCornerShape(16.dp)
            }
    )
}