package com.example.myevents.ui.screens.user

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.example.camera.utils.rememberPermission
import com.example.myevents.R
import com.example.myevents.data.database.User
import com.example.myevents.ui.MyEventsRoute
import com.example.myevents.ui.UserActions
import com.example.myevents.ui.UserState
import com.example.myevents.utils.rememberCameraLauncher

@Composable
fun RegisterScreen(
    navController: NavHostController,
    onRegisterAction: (String, String, Boolean) -> Unit,
    onRegisterCheck: (String) -> Boolean,
    actions: UserActions
) {
    var username by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var imageURI by remember { mutableStateOf("") }
    var isChecked by remember { mutableStateOf(false) }
    val errorEmpty = stringResource(R.string.error_empty_fields)
    val errorUsername = stringResource(R.string.error_us_used)
    val ctx = LocalContext.current
    val per_denied = stringResource(R.string.per_denied)

    //Camera
    val cameraLauncher = rememberCameraLauncher {
        imageUri -> imageURI = imageUri.toString()
    }

    val cameraPermission = rememberPermission(Manifest.permission.CAMERA) { status ->
        if (status.isGranted) {
            cameraLauncher.captureImage()
        } else {
            Toast.makeText(ctx, per_denied, Toast.LENGTH_SHORT).show()
        }
    }

    fun takePicture() {
        if (cameraPermission.status.isGranted) {
            cameraLauncher.captureImage()
        } else {
            cameraPermission.launchPermissionRequest()
        }
    }

    //UI
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(stringResource(R.string.name)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = surname,
            onValueChange = { surname = it },
            label = { Text(stringResource(R.string.surname)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text(stringResource(R.string.conf_pass)) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

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

        Spacer(modifier = Modifier.height(16.dp))

        Checkbox(
            checked = isChecked,
            onCheckedChange = { isChecked = it }
        )
        Text(text = stringResource(R.string.remember), modifier = Modifier.padding(16.dp))

        Spacer(modifier = Modifier.height(16.dp))

        FloatingActionButton(
            onClick = {
                if (username.isNotEmpty() && password.isNotEmpty() && password == confirmPassword) {
                    if (!onRegisterCheck(username)) {
                        actions.addUser(User(username = username, name = name, surname = surname, password = password, imageUri = imageURI))
                        onRegisterAction(username, password, isChecked)
                        navController.navigate(MyEventsRoute.Welcome.route)
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
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(text = stringResource(R.string.register))
        }

        Spacer(modifier = Modifier.height(16.dp))

        FloatingActionButton(
            onClick = {
                navController.navigate(MyEventsRoute.Login.route)
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(text = stringResource(R.string.already_have_acc))
        }
    }
}