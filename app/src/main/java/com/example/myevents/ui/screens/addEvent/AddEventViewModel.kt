package com.example.myevents.ui.screens.addEvent

import android.content.Context
import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myevents.data.database.Event
import com.example.myevents.data.database.Notification
import com.example.myevents.data.repositories.MyEventsRepository
import com.example.myevents.getLocationService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

data class AddEventState(
    val eventType: String,
    val title: String,
    val date: String,
    val imageUri: String?
)

class AddEventViewModel(
    private val repository: MyEventsRepository
) : ViewModel() {
    private var username: String? = null

    var state = MutableStateFlow(AddEventState("", "", "", ""))
        private set

    private val _latitude = MutableStateFlow(0.0)
    var latitude: StateFlow<Double> = _latitude
    private val _longitude = MutableStateFlow(0.0)
    var longitude: StateFlow<Double> = _longitude

    private var marker: Marker? = null

    init {
        viewModelScope.launch {
            username = repository.user.first()
            getLocationService().coordinates?.let {
                _latitude.value = it.latitude
                _longitude.value = it.longitude
            }
        }
    }

    fun updateUsername() {
        viewModelScope.launch {
            username = repository.user.first()
        }
    }

    fun checkCanAdd(): Boolean {
        return state.value.title.isNotEmpty()
                && state.value.date.isNotEmpty()
                && state.value.eventType.isNotEmpty();
    }

    fun setEventType(eventType: String) {
        state.value = state.value.copy(eventType = eventType)
    }

    fun setTitle(title: String) {
        state.value = state.value.copy(title = title)
    }

    fun setDate(date: String) {
        state.value = state.value.copy(date = date)
    }

    fun setImageUri(imageUri: String) {
        state.value = state.value.copy(imageUri = imageUri)
    }

    fun addEvent() {
        val event = Event(
            0,
            username.toString(),
            state.value.eventType,
            state.value.title,
            _longitude.value.toString(),
            _latitude.value.toString(),
            state.value.date,
            false,
            state.value.imageUri)
        viewModelScope.launch {
            repository.upsertEvent(event)
            repository.upsertNotification(
                Notification(
                    0,
                    event.username,
                    "${event.title};add",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    false
                )
            )
        }
        clearAddEventState()
    }

    fun clearAddEventState() {
        state.value = AddEventState("", "", "", "")
    }

    fun loadMap(mapView: MapView, currentLocation: GeoPoint, context: Context) {
        Configuration.getInstance().load(
            context,
            context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
        )
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.isClickable = true
        mapView.setMultiTouchControls(true)
        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT)
        setMarker(mapView, currentLocation, context)
        mapView.controller.setZoom(7.0)
        mapView.controller.setCenter(currentLocation)
    }

    @Throws(IOException::class)
    fun setMarker(mapView: MapView, currentLocation: GeoPoint, context: Context) {
        mapView.overlays.remove(marker)
        marker = Marker(mapView)
        setLocation(
            currentLocation.latitude,
            currentLocation.longitude,
            context
        )?.let { location ->
            marker!!.title = location.countryName
            marker!!.snippet = location.locality
        }
        marker!!.position = currentLocation
        marker!!.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        mapView.overlays.add(marker)
        _latitude.value = currentLocation.latitude
        _longitude.value = currentLocation.longitude
    }

    @Suppress("DEPRECATION")
    private fun setLocation(lat: Double, long: Double, context: Context): Address? {
        val gcd = Geocoder(context, Locale.getDefault())
        var addresses: List<Address>? = null
        try {
            addresses = gcd.getFromLocation(lat, long, 1)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return if (addresses!!.isNotEmpty()) {
            addresses[0]
        } else null
    }
}
