package com.example.myevents.ui.screens.addEvent

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.myevents.data.database.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Date

data class AddEventState(
    val destination: String = "",
    val date: String = "",
    val description: String = "",
    val imageUri: Uri = Uri.EMPTY,

    val showLocationDisabledAlert: Boolean = false,
    val showLocationPermissionDeniedAlert: Boolean = false,
    val showLocationPermissionPermanentlyDeniedSnackbar: Boolean = false,
    val showNoInternetConnectivitySnackbar: Boolean = false
) {
    val canSubmit get() = destination.isNotBlank() && date.isNotBlank() && description.isNotBlank()

    fun toEvent() = Event(
        place = destination,
        title =  description,
        username = date,
        eventType = "Event",
        isFavourite = false
    )
}

interface AddEventActions {
    fun setDestination(title: String)
    fun setDate(date: String)
    fun setDescription(description: String)
    fun setImageUri(imageUri: Uri)

    fun setShowLocationDisabledAlert(show: Boolean)
    fun setShowLocationPermissionDeniedAlert(show: Boolean)
    fun setShowLocationPermissionPermanentlyDeniedSnackbar(show: Boolean)
    fun setShowNoInternetConnectivitySnackbar(show: Boolean)
}

class AddEventViewModel : ViewModel() {
    private val _state = MutableStateFlow(AddEventState())
    val state = _state.asStateFlow()

    val actions = object : AddEventActions {
        override fun setDestination(title: String) =
            _state.update { it.copy(destination = title) }

        override fun setDate(date: String) =
            _state.update { it.copy(date = date) }

        override fun setDescription(description: String) =
            _state.update { it.copy(description = description) }

        override fun setImageUri(imageUri: Uri) =
            _state.update { it.copy(imageUri = imageUri) }

        override fun setShowLocationDisabledAlert(show: Boolean) =
            _state.update { it.copy(showLocationDisabledAlert = show) }

        override fun setShowLocationPermissionDeniedAlert(show: Boolean) =
            _state.update { it.copy(showLocationPermissionDeniedAlert = show) }

        override fun setShowLocationPermissionPermanentlyDeniedSnackbar(show: Boolean) =
            _state.update { it.copy(showLocationPermissionPermanentlyDeniedSnackbar = show) }

        override fun setShowNoInternetConnectivitySnackbar(show: Boolean) =
            _state.update { it.copy(showNoInternetConnectivitySnackbar = show) }
    }
}
