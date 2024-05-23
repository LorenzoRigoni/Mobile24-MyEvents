package com.example.myevents

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myevents.data.database.Event
import com.example.myevents.ui.EventsViewModel
import com.example.myevents.ui.MyEventsNavGraph
import com.example.myevents.ui.MyEventsRoute
import com.example.myevents.ui.UserViewModel
import com.example.myevents.ui.composables.AppBar
import com.example.myevents.ui.screens.addEvent.AddEventViewModel
import com.example.myevents.ui.screens.eventdetails.EventDetailsViewModel
import com.example.myevents.ui.screens.settings.SettingsViewModel
import com.example.myevents.ui.theme.MyEventsTheme
import com.example.myevents.utils.LocationService
import com.example.myevents.utils.Notification
import com.example.myevents.utils.channelID
import com.example.myevents.utils.messageExtra
import com.example.myevents.utils.notificationID
import com.example.myevents.utils.titleExtra
import org.koin.androidx.compose.koinViewModel
import java.util.Calendar

@SuppressLint("StaticFieldLeak")
private lateinit var locationService: LocationService

class MainActivity : FragmentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationService = LocationService(this)

        createNotificationChannel()

        setContent {
            val settingsVm = koinViewModel<SettingsViewModel>()
            val userVm = koinViewModel<UserViewModel>()

            val eventsVm = koinViewModel<EventsViewModel>()
            val eventsState by eventsVm.state.collectAsStateWithLifecycle()
            val notificationsState by eventsVm.notifState.collectAsStateWithLifecycle()

            val addEventVm = koinViewModel<AddEventViewModel>()
            val eventDetailsVm = koinViewModel<EventDetailsViewModel>()

            if (userVm.user != null) {
                if (settingsVm.preferences.reminderTime.isNotEmpty() && settingsVm.preferences.language.isNotEmpty()) {
                    scheduleNotification(
                        settingsVm.preferences.reminderTime.split(":")[0],
                        settingsVm.preferences.reminderTime.split(":")[1],
                        eventsVm.getNextEvent(),
                        settingsVm.preferences.language
                    )
                }
            }

            MyEventsTheme (
                darkTheme = if (settingsVm.preferences.theme == "Light") false else if(settingsVm.preferences.theme == "Dark") true else isSystemInDarkTheme()
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val backStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute by remember {
                        derivedStateOf {
                            MyEventsRoute.routes.find {
                                it.route == backStackEntry?.destination?.route
                            } ?: MyEventsRoute.Welcome
                        }
                    }

                    Scaffold(
                        topBar = {
                            AppBar(
                                navController,
                                currentRoute,
                                userVm,
                                eventsVm,
                                addEventVm,
                                eventDetailsVm,
                                settingsVm)
                        }
                    ) { contentPadding ->
                        MyEventsNavGraph(
                            navController,
                            userVm,
                            eventsVm,
                            eventsState,
                            notificationsState,
                            addEventVm,
                            eventDetailsVm,
                            settingsVm,
                            modifier =  Modifier.padding(contentPadding)
                        )
                    }
                }
            }
        }
    }

    private fun createNotificationChannel() {
        val name = "Notif Channel"
        val desc = "A Description of the Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun scheduleNotification(hours: String, minutes: String, nextEvent: Event?, language: String) {
        val intent = Intent(applicationContext, Notification::class.java)
        val title = when (language) {
            "English" -> "Daily reminder !"
            else -> "Promemoria giornaliero !"
        }
        val message = if (nextEvent != null) {
            when (language) {
                "English" -> "Your next event is ${nextEvent.title} on ${nextEvent.date}"
                else -> "Il tuo prossimo evento è ${nextEvent.title} il ${nextEvent.date}"
            }
        } else {
            when (language) {
                "English" -> "You have no upcoming events !"
                else -> "Non hai eventi in programma !"
            }
        }
        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time = getTime(hours, minutes)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
    }

    private fun getTime(hours: String, minutes: String): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR))
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH))
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, hours.toInt())
        calendar.set(Calendar.MINUTE, minutes.toInt())
        return calendar.timeInMillis
    }
}

fun getLocationService(): LocationService {
    return locationService
}
