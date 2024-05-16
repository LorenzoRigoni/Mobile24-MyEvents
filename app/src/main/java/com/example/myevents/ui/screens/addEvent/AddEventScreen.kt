package com.example.myevents.ui.screens.addEvent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.example.myevents.ui.MyEventsRoute
import com.example.myevents.utils.LocationService
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay

@Composable
fun AddEventScreen(
    addEventViewModel: AddEventViewModel,
    navController: NavHostController
) {
    var title by rememberSaveable { mutableStateOf("") }
    var eventType by rememberSaveable { mutableStateOf("") }
    var date by rememberSaveable { mutableStateOf("") }
    val latitude by addEventViewModel.latitude.collectAsState()
    val longitude by addEventViewModel.longitude.collectAsState()

    Scaffold(

    ) { contentPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(contentPadding)
                .padding(12.dp)
                .fillMaxSize()
        ) {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ){
                Text(
                    "Title of event",
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Row (
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    textStyle = MaterialTheme.typography.bodyMedium,
                    label = { Text(text = "Title")},
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
                    "Type of event",
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = eventType,
                    onValueChange = { eventType = it },
                    textStyle = MaterialTheme.typography.bodyMedium,
                    label = { Text(text = "Type")},
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
                    "Date of event",
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Row (
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    textStyle = MaterialTheme.typography.bodyMedium,
                    label = { Text(text = "Date")},
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(Modifier.size(24.dp))
            /*TODO: pick image from gallery*/
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ){
                Text(
                    "Position of event",
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
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Row (
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        if (title.isNotEmpty() && eventType.isNotEmpty() && date.isNotEmpty()) {
                            addEventViewModel.addEvent(
                                eventType,
                                title,
                                date,
                                "",
                                latitude.toString(),
                                longitude.toString()
                            )
                            navController.navigate(MyEventsRoute.Home.route)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                ) {
                    Text(
                        "Add the event!",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
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