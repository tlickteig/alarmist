package com.alarmist.Alarmist

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.alarmist.Alarmist.classes.Utilities
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                val context = LocalContext.current
                var activity = LocalContext.current as Activity
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                val alarmList = remember { mutableStateListOf<Alarm>() }

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
                                    var intent = Intent(
                                        context, NewAlarm::class.java
                                    )

                                    context.startActivity(intent)
                                })

                            TextButton(onClick = {
                                var test = DataAccess.returnAvailableAlarmId(activity)
                            }) {
                                Text("Test")
                            }
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
                        LazyColumn(
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            items(alarmList) {item ->
                                AlarmItem(item, alarmList)
                            }
                        }
                    }
                }

                OnLifecycleEvent { owner, event ->
                    when (event) {
                        Lifecycle.Event.ON_RESUME -> {
                            var tempAlarmList = DataAccess.returnAllAlarms(activity)
                            alarmList.clear()

                            for (alarm in tempAlarmList) {
                                alarmList.add(alarm)
                            }
                        }
                        else -> { /* Don't do anything */ }
                    }
                }
            }
        }
    }

    @Composable
    fun AlarmItem(alarm: Alarm, alarmList: SnapshotStateList<Alarm>) {
        var checked by remember { mutableStateOf(true) }
        var context = LocalContext.current
        var activity = LocalContext.current as Activity

        Column (
            modifier = Modifier.clickable {
                var intent = Intent(
                    context, NewAlarm::class.java
                )

                val alarmString = AlarmSerializer.serializeAlarm(alarm)
                intent.putExtra("alarm", alarmString)
                context.startActivity(intent)
            }
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(
                    text = alarm.title,
                    fontSize = 30.sp
                )

                Box(
                    contentAlignment = Alignment.CenterEnd,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Switch(
                        checked = checked,
                        onCheckedChange = {
                            checked = it

                            var tempAlarmList = DataAccess.returnAllAlarms(activity)
                            alarmList.clear()

                            for (alarm in tempAlarmList) {
                                alarmList.add(alarm)
                            }
                        }
                    )
                }
            }

            if (!alarm.subText.isNullOrBlank()) {
                Text(
                    text = alarm.subText,
                    modifier = Modifier.padding(5.dp)
                )
            } else {
                Text(
                    text = "",
                    modifier = Modifier.padding(5.dp)
                )
            }
        }

        HorizontalDivider(
            thickness = 2.dp
        )
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