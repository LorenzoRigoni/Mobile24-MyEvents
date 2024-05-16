package com.example.myevents.ui.screens.welcome

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.myevents.R
import com.example.myevents.ui.MyEventsRoute
import com.example.myevents.ui.UserState

@Composable
fun WelcomeScreen(
    state: UserState,
    navController: NavHostController,
    getImage: (String) -> String?
) {
    Scaffold { contentPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            if (state.isLogged || state.user.isNotEmpty()) {
                val tmpImage = getImage(state.user)
                val imageUri = if (tmpImage != null) Uri.parse(tmpImage) else Uri.EMPTY
                if (imageUri.path?.isNotEmpty() == true) {
                    AsyncImage(
                        ImageRequest.Builder(LocalContext.current)
                            .data(imageUri)
                            .crossfade(true)
                            .build(),
                        "User picture",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Image(
                        Icons.Outlined.Image,
                        stringResource(R.string.event_pic),
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondary),
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondary)
                            .padding(20.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = stringResource(R.string.hello) + " " + state.user + "!")
                Spacer(modifier = Modifier.height(16.dp))
                FloatingActionButton(
                    onClick = { navController.navigate(MyEventsRoute.Home.route) },
                    modifier = Modifier.padding(contentPadding),
                ) {
                    Text(
                        text = stringResource(R.string.browse),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                Text(text = stringResource(R.string.welcome))
                Spacer(modifier = Modifier.height(16.dp))
                FloatingActionButton(
                    onClick = { navController.navigate(MyEventsRoute.Login.route) },
                    modifier = Modifier.padding(contentPadding),
                ) {
                    Text(
                        text = stringResource(R.string.log),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}