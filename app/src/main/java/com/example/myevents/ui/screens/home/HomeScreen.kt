package com.example.myevents.ui.screens.home

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.myevents.R
import com.example.myevents.data.database.Event
import com.example.myevents.ui.EventsState
import com.example.myevents.ui.EventsViewModel
import com.example.myevents.ui.MyEventsRoute
import com.example.myevents.ui.UserState

@Composable
fun HomeScreen(
    eventsVM: EventsViewModel,
    navController: NavHostController) {

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primary,
                onClick = { navController.navigate(MyEventsRoute.AddEvent.route) }
            ) {
                Icon(Icons.Outlined.Add, stringResource(R.string.add_event))
            }
        },
    ) { contentPadding ->
        if (eventsVM.state.events.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(8.dp, 8.dp, 8.dp, 80.dp),
                modifier = Modifier.padding(contentPadding)
            ) {
                items(eventsVM.state.events) { item ->
                    EventItem(
                        item,
                        onClick = {
                            navController.navigate(MyEventsRoute.EventDetails.buildRoute(item.eventID.toString()))
                        }
                    )
                }
            }
        } else {
            NoEventsPlaceHolder(Modifier.padding(contentPadding))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventItem(
    item: Event,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .size(150.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val imageUri = Uri.parse(item.imageUri)
            if (imageUri.path?.isNotEmpty() == true) {
                AsyncImage(
                    ImageRequest.Builder(LocalContext.current)
                        .data(imageUri)
                        .crossfade(true)
                        .build(),
                    stringResource(R.string.event_pic),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(72.dp)
                )
            } else {
                Image(
                    Icons.Outlined.Image,
                    stringResource(R.string.event_pic),
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondary),
                    modifier = Modifier
                        .size(72.dp)
                        .background(MaterialTheme.colorScheme.secondary)
                        .padding(20.dp)
                )
            }
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                item.title,
                textAlign = TextAlign.Left
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                item.eventType,
                textAlign = TextAlign.Left
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                item.date,
                textAlign = TextAlign.Left
            )
        }
    }
}

@Composable
fun NoEventsPlaceHolder(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            stringResource(R.string.no_events),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            stringResource(R.string.tap_below),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }
}