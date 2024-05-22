package com.example.myevents.ui.screens.settings

import android.app.TimePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myevents.R
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun SettingsScreen(
    settingsVm: SettingsViewModel,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(12.dp)
            .fillMaxSize()
    ) {
        var theme by remember { mutableStateOf(settingsVm.preferences.theme) }
        var language by remember { mutableStateOf(settingsVm.preferences.language) }

        val scrollState = rememberScrollState()
        val expandedTheme = remember { mutableStateOf(false) }
        val expandedLanguage = remember { mutableStateOf(false) }

        val context = LocalContext.current
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        var reminderTime by remember { mutableStateOf(settingsVm.preferences.reminderTime) }
        val timePickerDialog = TimePickerDialog(context, { _, selectedHour: Int, selectedMinute: Int ->
            reminderTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            settingsVm.setReminderTime(reminderTime)
        }, hour, minute, true)

        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            Text(
                text = stringResource(R.string.generals),
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Divider()
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text(text = "Theme", modifier = Modifier.weight(1f))
                Box {
                    Button(
                        onClick = { expandedTheme.value = true },
                        Modifier.width(100.dp)
                    ) {
                        Text(text = theme)
                    }
                    DropdownMenu(
                        expanded = expandedTheme.value,
                        onDismissRequest = { expandedTheme.value = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.light)) },
                            onClick = {
                                theme = "Light"
                                expandedTheme.value = false
                                settingsVm.setTheme(theme)
                            },
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.dark)) },
                            onClick = {
                                theme = "Dark"
                                expandedTheme.value = false
                                settingsVm.setTheme(theme)
                            },
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.auto)) },
                            onClick = {
                                theme = "Auto"
                                expandedTheme.value = false
                                settingsVm.setTheme(theme)
                            },
                        )
                    }
                }
            }
            Divider()
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text(text = stringResource(R.string.language), modifier = Modifier.weight(1f))
                Box {
                    Button(
                        onClick = { expandedLanguage.value = true },
                        Modifier.width(100.dp)
                    ) {
                        Text(text = language)
                    }
                    DropdownMenu(
                        expanded = expandedLanguage.value,
                        onDismissRequest = { expandedLanguage.value = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.italian)) },
                            onClick = {
                                language = "Italian"
                                expandedLanguage.value = false
                                settingsVm.setLanguage(language, context)
                            },
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.english)) },
                            onClick = {
                                language = "English"
                                expandedLanguage.value = false
                                settingsVm.setLanguage(language, context)
                            },
                        )
                    }
                }
            }
            Divider()
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text(text = stringResource(R.string.reminder_time), modifier = Modifier.weight(1f))
                Button(
                    onClick = { timePickerDialog.show() },
                    Modifier.width(100.dp)
                ) {
                    Text(text = reminderTime.ifEmpty { "00:00" })
                }
            }
        }
    }
}
