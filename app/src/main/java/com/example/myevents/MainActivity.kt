package com.example.myevents

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
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
import org.koin.android.ext.android.get
import org.koin.androidx.compose.koinViewModel
import java.util.Calendar
import java.util.Date

class MainActivity : ComponentActivity() {
    private lateinit var locationService: LocationService

    private fun createNotificationChannel() {
        val name = "Notif Channel"
        val desc = "A Description of the Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun scheduleNotification()
    {
        val intent = Intent(applicationContext, Notification::class.java)
        val title = "Title"
        val message = "Notification"
        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time = getTime()
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
        showAlert(time, title, message)
    }

    private fun showAlert(time: Long, title: String, message: String)
    {
        val date = Date(time)
        val dateFormat = android.text.format.DateFormat.getLongDateFormat(applicationContext)
        val timeFormat = android.text.format.DateFormat.getTimeFormat(applicationContext)

        AlertDialog.Builder(this)
            .setTitle("Notification Scheduled")
            .setMessage(
                "Title: " + title +
                        "\nMessage: " + message +
                        "\nAt: " + dateFormat.format(date) + " " + timeFormat.format(date))
            .setPositiveButton("Okay"){_,_ ->}
            .show()
    }

    private fun getTime(): Long
    {
        val calendar = Calendar.getInstance()
        calendar.set(2024, 4, 21, 11, 18)
        return calendar.timeInMillis
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createNotificationChannel()
        scheduleNotification()

        locationService = get<LocationService>()

        setContent {
            MyEventsTheme {
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
                    val userVm = koinViewModel<UserViewModel>()

                    val eventsVm = koinViewModel<EventsViewModel>()
                    val eventsState by eventsVm.state.collectAsStateWithLifecycle()

                    val addEventVm = koinViewModel<AddEventViewModel>()
                    val eventDetailsVm = koinViewModel<EventDetailsViewModel>()
                    val settingsVm = koinViewModel<SettingsViewModel>()

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
}
