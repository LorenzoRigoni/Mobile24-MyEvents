package com.example.myevents.ui.screens.addEvent

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun AddEventScreen(
    state: AddEventState,
    actions: AddEventActions,
    navController: NavHostController
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primary,
                onClick = { navController.navigateUp() }
            ) {
                Icon(Icons.Outlined.Check, "Add Event")
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
                label = { Text("Destination") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Outlined.MyLocation, "Current location")
                    }
                }
            )
            OutlinedTextField(
                value = state.date,
                onValueChange = actions::setDate,
                label = { Text("Date") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.description,
                onValueChange = actions::setDescription,
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.size(24.dp))
            Button(
                onClick = { /*TODO*/ },
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
            ) {
                Icon(
                    Icons.Outlined.PhotoCamera,
                    contentDescription = "Camera icon",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Take a picture")
            }
            Spacer(Modifier.size(8.dp))
            Image(
                Icons.Outlined.Image,
                "Event picture",
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondary),
                modifier = Modifier
                    .size(128.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary)
                    .padding(36.dp)
            )
        }
    }
}
