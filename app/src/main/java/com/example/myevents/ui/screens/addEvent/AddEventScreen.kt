package com.example.myevents.ui.screens.addEvent

import android.app.TimePickerDialog
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.example.myevents.R
import com.example.myevents.utils.LocationService
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(
    addEventViewModel: AddEventViewModel,
    incrementNotificationBadge: () -> Unit,
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
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(contentPadding)
                .padding(12.dp)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ){
                Text(
                    stringResource(R.string.title_event),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Row (
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        addEventViewModel.setTitle(title)
                    },
                    textStyle = MaterialTheme.typography.bodyMedium,
                    label = { Text(text = stringResource(R.string.title_event))},
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(Modifier.size(24.dp))
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ){
                Text(
                    stringResource(R.string.type_event),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = eventType,
                    onValueChange = {
                        eventType = it
                        addEventViewModel.setEventType(eventType)
                    },
                    textStyle = MaterialTheme.typography.bodyMedium,
                    label = { Text(text = stringResource(R.string.type_event))},
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(Modifier.size(24.dp))
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ){
                Text(
                    stringResource(R.string.date_event),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Row (
                modifier = Modifier.fillMaxWidth()
            ) {
                DateTimePicker(
                    onDateTimeSelected = {
                        addEventViewModel.setDate(it.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                    },
                    context = LocalContext.current
                )
            }
            Spacer(Modifier.size(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Button(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                ) {
                    Text(text = stringResource(R.string.choose_image))
                }
            }
            Spacer(Modifier.size(24.dp))
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ){
                Text(
                    stringResource(R.string.position_event),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.size(70.dp))
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                OsmMapView(
                    addEventViewModel,
                    latitude,
                    longitude
                )
            }
            Spacer(Modifier.size(100.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePicker(
    onDateTimeSelected: (LocalDateTime) -> Unit,
    context: Context
) {
    var date by remember { mutableStateOf(LocalDate.now()) }
    var time by remember { mutableStateOf(LocalTime.now()) }
    var isDatePickerDialogShowing by remember { mutableStateOf(false) }
    var isTimePickerDialogShowing by remember { mutableStateOf(false) }

    Button(onClick = { isDatePickerDialogShowing = true }) {
        Text("Select Date and Time")
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
    val locService = LocationService(LocalContext.current)
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
        }
    )
}