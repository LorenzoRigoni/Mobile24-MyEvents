package com.example.myevents.ui.screens.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myevents.ui.MyEventsRoute

@Composable
fun LoginScreen(
    navController: NavHostController,
    onLoginAction: (String) -> Unit,
    onLoginCheck: (String, String) -> Boolean
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        FloatingActionButton(
            onClick = {
                if (username.isNotEmpty() && password.isNotEmpty()) {
                    if (onLoginCheck(username, password)) {
                        onLoginAction(username)
                        navController.navigate(MyEventsRoute.Welcome.route)
                    } else {
                        /*TODO: alert for error in login*/
                    }
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(text = "Login")
        }
        FloatingActionButton(
            onClick = { navController.navigate(MyEventsRoute.Register.route) },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(text = "You don't have an account? Register here!")
        }
    }
}