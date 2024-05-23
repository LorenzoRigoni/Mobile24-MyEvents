package com.example.myevents.ui.screens.profile

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.ModeEdit
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.camera.utils.rememberPermission
import com.example.myevents.R
import com.example.myevents.ui.EventsViewModel
import com.example.myevents.ui.UserViewModel
import com.example.myevents.utils.rememberCameraLauncher

@Composable
fun ProfileScreen(
    userVm: UserViewModel,
    eventsVm: EventsViewModel,
    navController: NavHostController
) {
    val ctx = LocalContext.current
    val per_denied = stringResource(R.string.per_denied)
    val changedName = stringResource(R.string.changed_name)
    val changedSurname = stringResource(R.string.changed_surname)

    val cameraLauncher = rememberCameraLauncher {
        imageUri -> userVm.setNewImage(imageUri.toString())
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

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    val scrollState = rememberScrollState()

    val openDialog = remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primary,
                onClick = { openDialog.value = true }
            ) {
                Icon(Icons.Outlined.ModeEdit, "Edit profile")
            }
        },
    ) { contentPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(contentPadding)
                .padding(25.dp)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            DrawUserImage(userVm, screenWidth, screenHeight, false, ::takePicture)

            Spacer(Modifier.size(8.dp))

            DrawUserInfos(
                userVm,
                screenWidth,
                screenHeight,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                )
            )

            if (openDialog.value) {
                Dialog(
                    onDismissRequest = {
                        openDialog.value = false
                    }
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.verticalScroll(scrollState)
                        ) {
                            Spacer(Modifier.size(8.dp))

                            DrawUserImage(userVm, screenWidth, screenHeight, true, ::takePicture)

                            Spacer(Modifier.size(8.dp))

                            DrawUserModifiableInfos(userVm, screenWidth, screenHeight, colors = CardDefaults.cardColors())

                            Spacer(Modifier.size(8.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.Bottom,
                            ) {
                                TextButton(
                                    onClick = {
                                        userVm.clearEditState()
                                        openDialog.value = false
                                    },
                                    modifier = Modifier.padding(8.dp),
                                ) {
                                    Text("Cancel")
                                }
                                TextButton(
                                    onClick = {
                                        val modifiedFields = userVm.saveEditState()
                                        modifiedFields.forEach {
                                            when (it.key) {
                                                "name" -> eventsVm.generateNotification("$changedName ${it.value}")
                                                "surname" -> eventsVm.generateNotification("$changedSurname ${it.value}")
                                            }
                                        }
                                        openDialog.value = false
                                    },
                                    modifier = Modifier.padding(8.dp),
                                ) {
                                    Text("Save")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DrawUserImage(userVm: UserViewModel, screenWidth: Dp, screenHeight: Dp, isEditable: Boolean, takePicture: () -> Unit) {
    val tmpImage = userVm.getImageUri(userVm.state.user)
    val imageUri = if (tmpImage != null) Uri.parse(tmpImage) else Uri.EMPTY
    if (imageUri.path?.isNotEmpty() == true) {
        AsyncImage(
            ImageRequest.Builder(LocalContext.current)
                .data(imageUri)
                .crossfade(true)
                .build(),
            "User picture",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .size(if (screenWidth > screenHeight) screenHeight / 2 else screenWidth / 2)
                .clip(CircleShape)
                .clickable {
                    if (isEditable) {
                        takePicture()
                    }
                }
        )
    } else {
        Image(
            Icons.Outlined.Image,
            stringResource(R.string.event_pic),
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondary),
            modifier = Modifier
                .size(if (screenWidth > screenHeight) screenHeight / 2 else screenWidth / 2)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondary)
                .padding(20.dp)
                .clickable {
                    if (isEditable) {
                        takePicture()
                    }
                }
        )
    }
}

@Composable
fun DrawUserInfos(userVm: UserViewModel, screenWidth: Dp, screenHeight: Dp, colors: CardColors) {
    userVm.user?.let {
        Card (
            colors = colors,
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .width(if (screenWidth > screenHeight) screenHeight / 2 else screenWidth / 2)
            ) {
                Text(
                    text = "Username",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    userVm.state.user,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.headlineLarge
                )
                Divider(
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 25.dp)
                )
                Text(
                    text = stringResource(R.string.name),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    it.name,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    style = MaterialTheme.typography.headlineLarge
                )
                Divider(
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 25.dp)
                )
                Text(
                    text = stringResource(R.string.surname),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    it.surname,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    style = MaterialTheme.typography.headlineLarge
                )
            }
        }
    }
}

@Composable
fun DrawUserModifiableInfos(userVm: UserViewModel, screenWidth: Dp, screenHeight: Dp, colors: CardColors) {
    userVm.user?.let {
        var editableName by rememberSaveable { mutableStateOf(it.name) }
        var editableSurname by rememberSaveable { mutableStateOf(it.surname) }

        Card (
            colors = colors,
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .width(if (screenWidth > screenHeight) screenHeight / 2 else screenWidth / 2)
            ) {
                Text(
                    text = "Username",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    userVm.state.user,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.headlineLarge
                )
                Divider(
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 25.dp)
                )
                OutlinedTextField(
                    value = editableName,
                    onValueChange = {
                        editableName = it
                        userVm.setNewName(it)
                    },
                    label = { Text(stringResource(R.string.name)) },
                    textStyle = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth()
                )
                Divider(
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 25.dp)
                )
                OutlinedTextField(
                    value = editableSurname,
                    onValueChange = {
                        editableSurname = it
                        userVm.setNewSurname(it)
                    },
                    label = { Text(stringResource(R.string.surname)) },
                    textStyle = MaterialTheme.typography.bodyLarge,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}