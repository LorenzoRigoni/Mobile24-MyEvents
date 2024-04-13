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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myevents.ui.MyEventsRoute

@Composable
fun WelcomeScreen(navController: NavHostController) {
    Scaffold { contentPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(text = "Ciao user!")
            Spacer(modifier = Modifier.height(16.dp))
            FloatingActionButton(
                onClick = { navController.navigate(MyEventsRoute.Home.route) },
                modifier = Modifier.padding(contentPadding),
            ) {
                Text(
                    text = "Sfoglia i miei eventi",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}