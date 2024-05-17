package com.example.myevents.ui.screens.eventdetails

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import com.example.myevents.data.repositories.MyEventsRepository
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.util.Locale

class EventDetailsViewModel(
    private val repository: MyEventsRepository
) : ViewModel() {

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