package com.example.myevents.ui.screens.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import com.example.myevents.data.database.User
import com.example.myevents.ui.MyEventsRoute
import com.example.myevents.ui.UserActions
import com.example.myevents.ui.UserState

@Composable
fun RegisterScreen(
    navController: NavHostController,
    onRegisterAction: (String) -> Unit,
    onRegisterCheck: (String) -> Boolean,
    actions: UserActions
) {
    var username by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

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
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = surname,
            onValueChange = { surname = it },
            label = { Text("Surname") },
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
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        /*TODO: CHECKBOX FOR REMIND ME*/

        FloatingActionButton(
            onClick = {
                if (username.isNotEmpty() && password.isNotEmpty() && password == confirmPassword) {
                    if (!onRegisterCheck(username)) {
                        actions.addUser(User(username = username, name = name, surname = surname, password = password))
                        /*TODO: check if user selected "remember me" checkbox*/
                        onRegisterAction(username)
                        navController.navigate(MyEventsRoute.Welcome.route)
                    } else {
                        /*TODO: alert for username witch is already taken*/
                    }
                } else {
                    /*TODO: alert for different passwords or empty fields*/
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(text = "Register")
        }
        FloatingActionButton(
            onClick = {
                navController.navigate(MyEventsRoute.Login.route)
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(text = "You already have an account? Login here!")
        }
    }
}
