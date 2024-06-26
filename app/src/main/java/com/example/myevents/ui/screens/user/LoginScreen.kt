package com.example.myevents.ui.screens.user

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import com.example.myevents.R
import com.example.myevents.ui.EventsViewModel
import com.example.myevents.ui.FilterEnum
import com.example.myevents.ui.MyEventsRoute
import com.example.myevents.ui.UserViewModel
import com.example.myevents.utils.BiometricAuthenticator
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun LoginScreen(
    navController: NavHostController,
    userVm: UserViewModel,
    eventsViewModel: EventsViewModel,
    updateAddEventVmUsername: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isChecked by remember { mutableStateOf(false) }
    val wrongUsername = stringResource(R.string.wrong_us)
    val coroutineScope = rememberCoroutineScope()
    val biometricAuthenticator = BiometricAuthenticator(LocalContext.current)
    val activity = LocalContext.current as FragmentActivity
    val noLogBio = stringResource(R.string.bio_not_roll)

    val scrollState = rememberScrollState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Image(
            painter = painterResource(id = R.mipmap.ic_launcher_foreground),
            contentDescription = "App Icon",
            modifier = Modifier.size(150.dp)
        )

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(32.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(32.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = { isChecked = it }
            )
            Text(
                text = stringResource(R.string.remember),
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        FloatingActionButton(
            onClick = {
                coroutineScope.launch {
                    if (username.isNotEmpty() && password.isNotEmpty()) {
                        userVm.checkLogin(username, password).join()
                        if (userVm.user != null) {
                            userVm.setLoggedUser(username, password, isChecked).join()
                            eventsViewModel.filter.value = FilterEnum.SHOW_FUTURE_EVENTS
                            eventsViewModel.updateNotifications()
                            eventsViewModel.updateAllEvents()
                            eventsViewModel.updateFutureEvents()
                            updateAddEventVmUsername()
                            navController.navigate(MyEventsRoute.Welcome.route) {
                                popUpTo(MyEventsRoute.Login.route) { inclusive = true }
                            }
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
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(),
            shape = RoundedCornerShape(32.dp)
        ) {
            Text(text = stringResource(R.string.log))
        }

        Spacer(Modifier.height(16.dp))

        FloatingActionButton(
            onClick = {
                biometricAuthenticator.promptBiometricAuthentication(
                    activity.getString(R.string.log),
                    activity.getString(R.string.log_bio),
                    activity,
                    onSuccess = {
                        coroutineScope.launch {
                            userVm.canLogWithBiometric().join()
                            if (userVm.bioUser.isNotEmpty()) {
                                userVm.setLoggedUser(userVm.bioUser, userVm.bioPassword, isChecked).join()
                                eventsViewModel.filter.value = FilterEnum.SHOW_FUTURE_EVENTS
                                eventsViewModel.updateNotifications()
                                eventsViewModel.updateAllEvents()
                                eventsViewModel.updateFutureEvents()
                                updateAddEventVmUsername()
                                navController.navigate(MyEventsRoute.Welcome.route) {
                                    popUpTo(MyEventsRoute.Login.route) { inclusive = true }
                                }
                            } else {
                                Toast.makeText(
                                    activity,
                                    noLogBio,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    },
                    onFailed = {
                        Toast.makeText(
                            activity,
                            activity.getString(R.string.bio_failed),
                            Toast.LENGTH_LONG
                        ).show()
                    },
                    onError = { _, message ->
                        Toast.makeText(
                            activity,
                            message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                )
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(32.dp)
        ) {
            Text(text = stringResource(R.string.log_bio))
        }

        Spacer(modifier = Modifier.height(16.dp))

        FloatingActionButton(
            onClick = {
                navController.navigate(MyEventsRoute.Register.route) {
                    popUpTo(MyEventsRoute.Login.route) { inclusive = true }
                }
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(32.dp)
        ) {
            Text(text = stringResource(R.string.no_account))
        }
    }
}