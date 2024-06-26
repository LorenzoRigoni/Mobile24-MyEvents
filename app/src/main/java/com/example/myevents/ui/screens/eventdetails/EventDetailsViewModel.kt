package com.example.myevents.ui.screens.eventdetails

import android.content.Context
import android.location.Address
import android.location.Geocoder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myevents.data.database.Event
import com.example.myevents.data.database.Notification
import com.example.myevents.data.repositories.MyEventsRepository
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

data class EventEditState(val newTitle: String, val newType: String, val newDate: String)

class EventDetailsViewModel(
    private val repository: MyEventsRepository
) : ViewModel() {

    var eventEditState by mutableStateOf(EventEditState("", "", ""))
        private set

    private var singleEventToDelete: Int? = null

    fun editEvent(originalEvent: Event) {
        if (originalEvent != Event(
            originalEvent.eventID,
            originalEvent.username,
            eventEditState.newType,
            eventEditState.newTitle,
            originalEvent.longitude,
            originalEvent.latitude,
            eventEditState.newDate,
            originalEvent.isFavourite,
            originalEvent.imageUri
        )) {
            viewModelScope.launch {
                repository.upsertEvent(
                    Event(
                        originalEvent.eventID,
                        originalEvent.username,
                        if (eventEditState.newType == "" || eventEditState.newType == originalEvent.eventType) originalEvent.eventType else eventEditState.newType,
                        if (eventEditState.newTitle == "" || eventEditState.newTitle == originalEvent.title) originalEvent.title else eventEditState.newTitle,
                        originalEvent.longitude,
                        originalEvent.latitude,
                        if (eventEditState.newDate == "" || eventEditState.newDate == originalEvent.date) originalEvent.date else eventEditState.newDate,
                        originalEvent.isFavourite,
                        originalEvent.imageUri
                    )
                )
            }
            clearEditState()
        }
    }

    fun setSingleEventToDelete(eventId: Int) {
        singleEventToDelete = eventId
    }

    fun deleteSingleEvent() {
        if (singleEventToDelete == null) return
        viewModelScope.launch {
            val id = singleEventToDelete!!
            val event = repository.getEventFromId(id)
            repository.deleteEventFromId(id)
            repository.upsertNotification(
                Notification(
                    0,
                    event!!.username,
                    "${event.title};delete",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    false
                )
            )
        }
        singleEventToDelete = null
    }

    fun setNewTitle(newTitle: String) {
        eventEditState = eventEditState.copy(newTitle = newTitle)
    }

    fun setNewType(newType: String) {
        eventEditState = eventEditState.copy(newType = newType)
    }

    fun setNewDate(newDate: String) {
        eventEditState = eventEditState.copy(newDate = newDate)
    }

    fun clearEditState() {
        eventEditState = EventEditState("", "", "")
    }

    fun updateIsFavourite(isFavourite: Boolean, eventId: Int) {
        viewModelScope.launch {
            repository.updateIsFavourite(isFavourite, eventId)
        }
    }

    fun openMap(latitude: String, longitude: String, mapView: MapView, context: Context) {
        Configuration.getInstance().load(
            context,
            context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
        )
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.isClickable = true
        mapView.setMultiTouchControls(true)
        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT)

        val lat = latitude.toDouble()
        val long = longitude.toDouble()
        val currentLocation = GeoPoint(lat, long)
        mapView.controller.setZoom(7.0)
        mapView.controller.setCenter(currentLocation)
        val startMarker = Marker(mapView)
        if (setLocation(lat, long, context) != null) {
            startMarker.title = setLocation(lat, long, context)!!.countryName
            startMarker.snippet = setLocation(lat, long, context)!!.locality
        }
        startMarker.position = currentLocation
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        mapView.overlays.add(startMarker)
    }

    @Suppress("DEPRECATION")
    private fun setLocation(lat: Double, long: Double, context: Context) : Address? {
        val gcd = Geocoder(context, Locale.getDefault())
        val addresses: List<Address>? = gcd.getFromLocation(lat, long, 1)
        assert(addresses != null)
        return if (addresses!!.isNotEmpty()) {
            addresses[0]
        } else null
    }
}