package com.example.myevents.ui.screens.addEvent

import android.app.TimePickerDialog
import android.content.Context
import android.icu.number.Scale
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.example.myevents.R
import com.example.myevents.getLocationService
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import com.example.myevents.utils.dateTimeFormatterFromLocalDateTime

@Composable
fun AddEventScreen(
    addEventViewModel: AddEventViewModel,
    navController: NavHostController
) {
    var title by rememberSaveable { mutableStateOf("") }
    var eventType by rememberSaveable { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val latitude by addEventViewModel.latitude.collectAsState()
    val longitude by addEventViewModel.longitude.collectAsState()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {uri ->
            selectedImageUri = uri
            addEventViewModel.setImageUri(selectedImageUri.toString())
        }
    )

    val scrollState = rememberScrollState()

    Scaffold { contentPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .padding(contentPadding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            ImagePicker(
                selectedImageUri = selectedImageUri,
                modifier = Modifier
                    .align(Alignment.Start)
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    addEventViewModel.setTitle(title)
                },
                textStyle = MaterialTheme.typography.bodyMedium,
                label = { Text(text = stringResource(R.string.title_event))},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = eventType,
                onValueChange = {
                    eventType = it
                    addEventViewModel.setEventType(eventType)
                },
                textStyle = MaterialTheme.typography.bodyMedium,
                label = { Text(text = stringResource(R.string.type_event))},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            DateTimePicker(
                onDateTimeSelected = {
                    addEventViewModel.setDate(it.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                },
                context = LocalContext.current,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier
                    .align(Alignment.Start)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(32.dp)
            ) {
                Text(text = stringResource(R.string.choose_image))
            }

            Spacer(modifier = Modifier.height(4.dp))

            var isMapDialogShowing by remember { mutableStateOf(false) }

            Button(
                onClick = { isMapDialogShowing = true },
                modifier = Modifier
                    .align(Alignment.Start)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(32.dp)
            ) {
                Text(stringResource(R.string.show_map))
            }

            if (isMapDialogShowing) {
                Dialog(
                    onDismissRequest = { isMapDialogShowing = false },
                ) {
                    Box(
                        modifier = Modifier
                            .height(LocalConfiguration.current.screenWidthDp.dp)
                    ) {
                        OsmMapView(
                            addEventViewModel,
                            latitude,
                            longitude
                        )
                    }
                    Row {
                        Button(
                            onClick = {
                                isMapDialogShowing = false
                            },
                            modifier = Modifier
                                .padding(8.dp)
                        ) {
                            Text("OK")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ImagePicker(
    selectedImageUri: Uri?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(400.dp)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        if (selectedImageUri != null) {
            val painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current)
                    .data(data = selectedImageUri)
                    .apply(block = fun ImageRequest.Builder.() { crossfade(true) })
                    .build()
            )

            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )
        } else {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 4.dp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePicker(
    onDateTimeSelected: (LocalDateTime) -> Unit,
    context: Context,
    modifier: Modifier
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
            "${stringResource(R.string.select_date)}: ${dateTimeFormatterFromLocalDateTime(LocalDateTime.of(date, time))}"
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
fun OsmMapView(addEventViewModel: AddEventViewModel, latitude: Double, longitude: Double) {
    val locService = getLocationService()
    var lat = if (latitude == 0.0) locService.coordinates?.latitude else latitude
    var long = if (longitude == 0.0) locService.coordinates?.longitude else longitude
    val currLocation = lat?.let { long?.let { it1 -> GeoPoint(it, it1) } }
    AndroidView(
        factory = { context ->
            MapView(context).apply {
                if (currLocation != null) {
                    addEventViewModel.loadMap(this, currLocation, context)
                }
                this.overlayManager.add(MapEventsOverlay(object : MapEventsReceiver {
                    override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                        if (p != null) {
                            lat = p.latitude
                            long = p.longitude
                            addEventViewModel.setMarker(
                                this@apply,
                                GeoPoint(lat!!, long!!),
                                context
                            )
                        }
                        return true
                    }

                    override fun longPressHelper(p: GeoPoint?): Boolean {
                        return false
                    }
                }))
            }
        },
        modifier = Modifier
            .graphicsLayer {
                clip = true
                shape = RoundedCornerShape(16.dp)
            }
    )
}