package com.example.myevents.ui.screens.user

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myevents.R
import com.example.myevents.ui.EventsViewModel
import com.example.myevents.ui.FilterEnum
import com.example.myevents.ui.MyEventsRoute
import com.example.myevents.ui.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    navController: NavHostController,
    userVm: UserViewModel,
    eventsViewModel: EventsViewModel
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isChecked by remember { mutableStateOf(false) }
    val wrongUsername = stringResource(R.string.wrong_us)
    val coroutineScope = rememberCoroutineScope()

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

        Checkbox(
            checked = isChecked,
            onCheckedChange = { isChecked = it }
        )
        Text(text = stringResource(R.string.remember), modifier = Modifier.padding(16.dp))

        Spacer(modifier = Modifier.height(16.dp))

        FloatingActionButton(
            onClick = {
                coroutineScope.launch {
                    if (username.isNotEmpty() && password.isNotEmpty()) {
                        if (userVm.checkLogin(username, password)) {
                            userVm.setLoggedUser(username, isChecked)
                            eventsViewModel.updateEvents(FilterEnum.SHOW_FUTURE_EVENTS)
                            navController.navigate(MyEventsRoute.Welcome.route)
                        } else {
                            Toast.makeText(
                                navController.context,
                                wrongUsername,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(text = stringResource(R.string.log))
        }

        Spacer(modifier = Modifier.height(16.dp))

        FloatingActionButton(
            onClick = { navController.navigate(MyEventsRoute.Register.route) },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(text = stringResource(R.string.no_account))
        }
    }
}