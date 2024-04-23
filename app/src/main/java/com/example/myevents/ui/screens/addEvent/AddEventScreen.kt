package com.example.myevents.ui.screens.addEvent

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.camera.utils.PermissionStatus
import com.example.camera.utils.rememberPermission
import com.example.myevents.R
import com.example.myevents.data.remote.OSMDataSource
import com.example.myevents.ui.composables.ImageWithPlaceholder
import com.example.myevents.ui.composables.Size
import com.example.myevents.utils.LocationService
import com.example.myevents.utils.rememberCameraLauncher
import org.koin.compose.koinInject

@Composable
fun AddEventScreen(
    state: AddEventState,
    actions: AddEventActions,
    onSubmit: () -> Unit,
    navController: NavHostController
) {
    val ctx = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val permDenied = stringResource(R.string.per_denied)
    val locPermReq = stringResource(R.string.loc_perm_req)
    val gosettings = stringResource(R.string.go_set)
    val noInternet = stringResource(R.string.no_internet)

    // Camera

    val cameraLauncher = rememberCameraLauncher { imageUri ->
        actions.setImageUri(imageUri)
    }

    val cameraPermission = rememberPermission(Manifest.permission.CAMERA) { status ->
        if (status.isGranted) {
            cameraLauncher.captureImage()
        } else {
            Toast.makeText(ctx, permDenied, Toast.LENGTH_SHORT).show()
        }
    }

    fun takePicture() {
        if (cameraPermission.status.isGranted) {
            cameraLauncher.captureImage()
        } else {
            cameraPermission.launchPermissionRequest()
        }
    }

    // Location

    val locationService = koinInject<LocationService>()

    val locationPermission = rememberPermission(
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) { status ->
        when (status) {
            PermissionStatus.Granted ->
                locationService.requestCurrentLocation()

            PermissionStatus.Denied ->
                actions.setShowLocationPermissionDeniedAlert(true)

            PermissionStatus.PermanentlyDenied ->
                actions.setShowLocationPermissionPermanentlyDeniedSnackbar(true)

            PermissionStatus.Unknown -> {}
        }
    }

    fun requestLocation() {
        if (locationPermission.status.isGranted) {
            locationService.requestCurrentLocation()
        } else {
            locationPermission.launchPermissionRequest()
        }
    }

    LaunchedEffect(locationService.isLocationEnabled) {
        actions.setShowLocationDisabledAlert(locationService.isLocationEnabled == false)
    }

    // HTTP

    val osmDataSource = koinInject<OSMDataSource>()

    fun isOnline(): Boolean {
        val connectivityManager = ctx
            .applicationContext
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true ||
                capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
    }
    fun openWirelessSettings() {
        val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        if (intent.resolveActivity(ctx.applicationContext.packageManager) != null) {
            ctx.applicationContext.startActivity(intent)
        }
    }

    LaunchedEffect(locationService.coordinates) {
        if (locationService.coordinates == null) return@LaunchedEffect
        if (!isOnline()) {
            actions.setShowNoInternetConnectivitySnackbar(true)
            return@LaunchedEffect
        }
        val place = osmDataSource.getPlace(locationService.coordinates!!)
        actions.setDestination(place.displayName)
    }

    // UI

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primary,
                onClick = {
                    if (!state.canSubmit) return@FloatingActionButton
                    onSubmit()
                    navController.navigateUp()
                }
            ) {
                Icon(Icons.Outlined.Check, stringResource(R.string.add_event))
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
            OutlinedTextField(
                value = state.destination,
                onValueChange = actions::setDestination,
                label = { Text(stringResource(R.string.dest)) },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = ::requestLocation) {
                        Icon(Icons.Outlined.MyLocation, "Current location")
                    }
                }
            )
            OutlinedTextField(
                value = state.date,
                onValueChange = actions::setDate,
                label = { Text(stringResource(R.string.date)) },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.description,
                onValueChange = actions::setDescription,
                label = { Text(stringResource(R.string.desc)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.size(24.dp))
            Button(
                onClick = ::takePicture,
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
            ) {
                Icon(
                    Icons.Outlined.PhotoCamera,
                    contentDescription = "Camera icon",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(stringResource(R.string.take_pic))
            }
            Spacer(Modifier.size(8.dp))
            ImageWithPlaceholder(state.imageUri, Size.Lg)
        }
    }

    if (state.showLocationDisabledAlert) {
        AlertDialog(
            title = { Text(stringResource(R.string.loc_disabled)) },
            text = { Text(stringResource(R.string.loc_alert)) },
            confirmButton = {
                TextButton(onClick = {
                    locationService.openLocationSettings()
                    actions.setShowLocationDisabledAlert(false)
                }) {
                    Text(stringResource(R.string.enable))
                }
            },
            dismissButton = {
                TextButton(onClick = { actions.setShowLocationDisabledAlert(false) }) {
                    Text(stringResource(R.string.dismiss))
                }
            },
            onDismissRequest = { actions.setShowLocationDisabledAlert(false) }
        )
    }

    if (state.showLocationPermissionDeniedAlert) {
        AlertDialog(
            title = { Text(stringResource(R.string.loc_perm_disabled)) },
            text = { Text(stringResource(R.string.loc_perm_alert)) },
            confirmButton = {
                TextButton(onClick = {
                    locationPermission.launchPermissionRequest()
                    actions.setShowLocationPermissionDeniedAlert(false)
                }) {
                    Text(stringResource(R.string.grant))
                }
            },
            dismissButton = {
                TextButton(onClick = { actions.setShowLocationPermissionDeniedAlert(false) }) {
                    Text(stringResource(R.string.dismiss))
                }
            },
            onDismissRequest = { actions.setShowLocationPermissionDeniedAlert(false) }
        )
    }

    if (state.showLocationPermissionPermanentlyDeniedSnackbar) {
        LaunchedEffect(snackbarHostState) {
            val res = snackbarHostState.showSnackbar(
                locPermReq,
                gosettings,
                duration = SnackbarDuration.Long
            )
            if (res == SnackbarResult.ActionPerformed) {
                ctx.startActivity(
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", ctx.packageName, null)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                )
            }
            actions.setShowLocationPermissionPermanentlyDeniedSnackbar(false)
        }
    }

    if (state.showNoInternetConnectivitySnackbar) {
        LaunchedEffect(snackbarHostState) {
            val res = snackbarHostState.showSnackbar(
                message = noInternet,
                actionLabel = gosettings,
                duration = SnackbarDuration.Long
            )
            if (res == SnackbarResult.ActionPerformed) {
                openWirelessSettings()
            }
            actions.setShowNoInternetConnectivitySnackbar(false)
        }
    }
}
