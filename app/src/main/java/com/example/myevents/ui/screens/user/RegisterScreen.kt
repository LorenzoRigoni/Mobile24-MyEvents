package com.example.myevents.ui.screens.user

import android.Manifest
import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myevents.R
import com.example.myevents.data.database.User
import com.example.myevents.ui.EventsViewModel
import com.example.myevents.ui.FilterEnum
import com.example.myevents.ui.MyEventsRoute
import com.example.myevents.ui.UserActions
import com.example.myevents.ui.UserViewModel
import com.example.myevents.utils.rememberCameraLauncher
import com.example.myevents.utils.rememberPermission
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    navController: NavHostController,
    userVm: UserViewModel,
    actions: UserActions,
    eventsViewModel: EventsViewModel,
    updateAddEventVmUsername: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var imageURI by remember { mutableStateOf("") }
    var isChecked by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val errorEmpty = stringResource(R.string.error_empty_fields)
    val errorUsername = stringResource(R.string.error_us_used)
    val ctx = LocalContext.current
    val perDenied = stringResource(R.string.per_denied)

    //Camera
    val cameraLauncher = rememberCameraLauncher {
        imageUri -> imageURI = imageUri.toString()
    }

    val cameraPermission = rememberPermission(Manifest.permission.CAMERA) { status ->
        if (status.isGranted) {
            cameraLauncher.captureImage()
        } else {
            Toast.makeText(ctx, perDenied, Toast.LENGTH_SHORT).show()
        }
    }

    fun takePicture() {
        if (cameraPermission.status.isGranted) {
            cameraLauncher.captureImage()
        } else {
            cameraPermission.launchPermissionRequest()
        }
    }

    val scrollState = rememberScrollState()

    //UI
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(32.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(stringResource(R.string.name)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(32.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = surname,
            onValueChange = { surname = it },
            label = { Text(stringResource(R.string.surname)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(32.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(32.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text(stringResource(R.string.conf_pass)) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(32.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row (
            modifier = Modifier.fillMaxWidth(),
        ) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = { isChecked = it }
            )
            Text(
                text = stringResource(R.string.remember),
                modifier = Modifier.align(Alignment.CenterVertically).weight(1f)
            )
            Button(
                onClick = ::takePicture,
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding
            ) {
                Icon(
                    Icons.Outlined.PhotoCamera,
                    contentDescription = "Camera icon",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(text = stringResource(R.string.user_pic))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        FloatingActionButton(
            onClick = {
                coroutineScope.launch {
                    if (username.isNotEmpty() && password.isNotEmpty() && password == confirmPassword) {
                        if (!userVm.isUsernameAlreadyTaken(username)) {
                            actions.addUser(
                                User(
                                    username = username,
                                    name = name,
                                    surname = surname,
                                    password = password,
                                    imageUri = imageURI
                                )
                            )
                            userVm.setLoggedUser(username, password, isChecked).join()
                            eventsViewModel.updateEvents(FilterEnum.SHOW_FUTURE_EVENTS)
                            eventsViewModel.updateNotifications()
                            eventsViewModel.updateAllEvents()
                            eventsViewModel.updateFutureEvents()
                            updateAddEventVmUsername()
                            navController.navigate(MyEventsRoute.Welcome.route) {
                                popUpTo(MyEventsRoute.Register.route) { inclusive = true }
                            }
                        } else {
                            Toast.makeText(
                                navController.context,
                                errorUsername,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            navController.context,
                            errorEmpty,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(),
            shape = RoundedCornerShape(32.dp)
        ) {
            Text(text = stringResource(R.string.register))
        }

        Spacer(modifier = Modifier.height(16.dp))

        FloatingActionButton(
            onClick = {
                navController.navigate(MyEventsRoute.Login.route) {
                    popUpTo(MyEventsRoute.Register.route) { inclusive = true }
                }
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(32.dp)
        ) {
            Text(text = stringResource(R.string.already_have_acc))
        }
    }
}