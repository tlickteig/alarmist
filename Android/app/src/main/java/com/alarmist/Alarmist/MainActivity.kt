package com.alarmist.Alarmist

import android.content.Intent
import com.alarmist.Alarmist.classes.CustomColors
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.End
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role.Companion.Switch
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alarmist.Alarmist.classes.Alarm
import com.alarmist.Alarmist.classes.CustomFonts
import com.alarmist.Alarmist.classes.FontAwesomeConstants
import com.alarmist.Alarmist.classes.Utilities
import kotlinx.coroutines.launch
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                val context = LocalContext.current
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()

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
                        val alarmList = remember { mutableStateListOf<Alarm>() }
                        var tempAlarmList = Utilities.returnAllAlarms()

                        for (alarm in tempAlarmList) {
                            alarmList.add(alarm)
                        }

                        LazyColumn(
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            items(alarmList) {item ->
                                AlarmItem(item, alarmList)
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun AlarmItem(alarm: Alarm, alarmList: SnapshotStateList<Alarm>) {
        var checked by remember { mutableStateOf(true) }

        Column (
            modifier = Modifier.clickable {
                println("sfsafasdf")
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
                    text = alarm.name,
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

                            var tempAlarmList = Utilities.returnAllAlarms()
                            alarmList.clear()

                            for (alarm in tempAlarmList) {
                                alarmList.add(alarm)
                            }
                        }
                    )
                }
            }

            if (!alarm.willGoOff.isNullOrBlank()) {
                Text(
                    text = alarm.willGoOff,
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
}