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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.collectAsState
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
                    "Add a new event!",
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
                    label = { Text(text = "Title of event")},
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(Modifier.size(24.dp))
        }
    }

    /*if (state.showLocationDisabledAlert) {
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
    }*/
}
