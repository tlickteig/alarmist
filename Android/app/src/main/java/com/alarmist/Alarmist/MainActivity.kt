package com.alarmist.Alarmist

import android.app.Activity
import android.app.Notification
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.alarmist.Alarmist.classes.AlarmSerializer
import com.alarmist.Alarmist.objects.Alarm
import com.alarmist.Alarmist.classes.CustomFonts
import com.alarmist.Alarmist.classes.DataAccess
import com.alarmist.Alarmist.classes.FontAwesomeConstants
import com.alarmist.Alarmist.classes.NotificationHelper
import com.alarmist.Alarmist.classes.Utilities
import com.alarmist.Alarmist.composables.AlarmItem
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                val context = LocalContext.current
                val activity = LocalContext.current as Activity
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                var alarmList by remember { mutableStateOf(listOf(Alarm())) }
                val showNotificationAlert = remember { mutableStateOf(false) }
                val isAnAlarmGoingOff = remember { mutableStateOf(false ) }
                Utilities.setBackgroundThread(context)

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet(
                            modifier = Modifier.padding(end = 40.dp)
                        ) {
                            Text("Alarmist", modifier = Modifier.padding(16.dp))
                            NavigationDrawerItem(
                                label = { Text("New Alarm") },
                                selected = false,
                                onClick = {
                                    val intent = Intent(
                                        context, NewAlarm::class.java
                                    )

                                    context.startActivity(intent)
                                }
                            )
                        }
                    }
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    Row (
                                        horizontalArrangement = Arrangement.Start,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        TextButton(
                                            onClick = {
                                                scope.launch {
                                                    drawerState.apply {
                                                        open()
                                                    }
                                                }
                                            }
                                        ) {
                                            Text(
                                                text = FontAwesomeConstants.MENU_ICON,
                                                fontFamily = CustomFonts.FontAwesome,
                                                fontSize = 20.sp
                                            )
                                        }
                                        Text(
                                            text = "My Alarms"
                                        )
                                    }
                                }
                            )
                        }
                    ) { innerPadding ->
                        Column(
                            modifier = Modifier.background(Color.Yellow)
                        ) {
                            LazyColumn(
                                modifier = Modifier.padding(innerPadding)
                            ) {
                                items(alarmList) { item ->
                                    AlarmItem(item)
                                }

                                if (showNotificationAlert.value) {
                                    item {
                                        Spacer(modifier = Modifier.height(100.dp))
                                    }
                                }
                            }
                        }

                        if (showNotificationAlert.value && !isAnAlarmGoingOff.value) {
                            Row(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(innerPadding),
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.Red)
                                ) {
                                    Text("Notifications are not enabled. This may severely impact functionality")
                                    TextButton(
                                        onClick = {
                                            NotificationHelper.openNotificationSettings(context)
                                        }
                                    ) {
                                        Text("Tap to enable")
                                    }
                                }
                            }
                        }

                        if (isAnAlarmGoingOff.value) {
                            Row(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(innerPadding),
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.Yellow)
                                        .clickable {
                                            val alarm = Utilities.currentAlarmNotification
                                            val alarmString = AlarmSerializer.serializeAlarm(alarm)
                                            val stopIntent = Intent(
                                                context, AlarmGoingOff::class.java
                                            )
                                            stopIntent.putExtra("alarmString", alarmString)
                                            context.startActivity(stopIntent)
                                        }
                                ) {
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Text("Tap to view alarm currently going off")
                                    Spacer(modifier = Modifier.height(5.dp))
                                }
                            }
                        }
                    }
                }

                OnLifecycleEvent { owner, event ->
                    Utilities.setBackgroundThread(context)
                    alarmList = mutableListOf()
                    alarmList = DataAccess.returnAllAlarms(activity).toMutableList()

                    val areNotificationsEnabled = NotificationHelper.areNotificationsEnabled(context)
                    showNotificationAlert.value = !areNotificationsEnabled

                    when (event) {
                        Lifecycle.Event.ON_RESUME -> {
                            scope.launch {
                                drawerState.apply {
                                    close()
                                }
                            }
                        }
                        Lifecycle.Event.ON_START -> {
                            if (!areNotificationsEnabled) {
                                NotificationHelper.requestNotificationPermission(activity)
                            }

                            Timer().schedule(object: TimerTask() {
                                override fun run() {
                                    isAnAlarmGoingOff.value = Utilities.isAnAlarmGoingOff()
                                    alarmList = DataAccess.returnAllAlarms(activity).toMutableList()
                                }
                            }, 0L, 1000L)
                        }
                        else -> { /* Don't do anything */ }

                    }
                }
            }
        }
    }

    // https://stackoverflow.com/questions/66546962/jetpack-compose-how-do-i-refresh-a-screen-when-app-returns-to-foreground
    @Composable
    fun OnLifecycleEvent(onEvent: (owner: LifecycleOwner, event: Lifecycle.Event) -> Unit) {
        val eventHandler = rememberUpdatedState(onEvent)
        val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)

        DisposableEffect(lifecycleOwner.value) {
            val lifecycle = lifecycleOwner.value.lifecycle
            val observer = LifecycleEventObserver { owner, event ->
                eventHandler.value(owner, event)
            }

            lifecycle.addObserver(observer)
            onDispose {
                lifecycle.removeObserver(observer)
            }
        }
    }
}