package com.example.myevents.ui.screens.welcome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myevents.R
import com.example.myevents.ui.MyEventsRoute
import com.example.myevents.ui.UserState

@Composable
fun WelcomeScreen(
    state: UserState,
    navController: NavHostController,
    logout: () -> Unit
) {
    Scaffold { contentPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            if (state.user.isNotEmpty()) {
                Text(text = stringResource(R.string.hello) + state.user + "!")
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
                FloatingActionButton(
                    onClick = {
                        logout()
                        navController.navigate(MyEventsRoute.Welcome.route)
                    },
                    modifier = Modifier.padding(contentPadding),
                ) {
                    Text(
                        text = stringResource(R.string.logout),
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