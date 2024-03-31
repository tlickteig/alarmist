package com.alarmist.Alarmist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.semantics.Role.Companion.RadioButton
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.alarmist.Alarmist.classes.AlarmSchedule
import com.alarmist.Alarmist.classes.CustomColors
import com.commandiron.wheel_picker_compose.WheelTimePicker
import com.commandiron.wheel_picker_compose.core.TimeFormat
import com.commandiron.wheel_picker_compose.core.WheelPickerDefaults

@OptIn(ExperimentalMaterial3Api::class)
class NewAlarm : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var isWeekPickerOpen = remember { mutableStateOf(false) }
            var isCalendarOpen = remember { mutableStateOf(false) }

            MaterialTheme {
                var alarmName by remember { mutableStateOf(TextFieldValue("")) }
                var scheduleMode = remember { mutableStateOf(AlarmSchedule.SCHEDULED) }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            colors = TopAppBarDefaults.mediumTopAppBarColors(
                                containerColor = CustomColors.TitleBarColor
                            ),
                            title = {
                                Text("Create new Alarm")
                            },
                            navigationIcon = {
                                IconButton(
                                    onClick = {
                                        finish()
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = CustomColors.TextColor
                                    )
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier.padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TextField(
                            value = alarmName,
                            onValueChange = { value ->
                                alarmName = value
                                println(alarmName)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        )

                        WheelTimePicker(
                            timeFormat = TimeFormat.AM_PM,
                            size = DpSize(250.dp, 250.dp),
                            textColor = Color.Blue,
                            selectorProperties = WheelPickerDefaults.selectorProperties(
                                enabled = false
                            )
                        ) {
                            snappedTime ->

                        }

                        Row() {
                            TextButton(
                                onClick = { isWeekPickerOpen.value = true }
                            ) {
                                Text("Select Days...", color = CustomColors.TextButtonColor)
                            }

                            IconButton(
                                onClick = { /*TODO*/ }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CalendarMonth,
                                    contentDescription = "Choose dates",
                                    tint = CustomColors.TextColor
                                )
                            }
                        }
                    }
                }

                if (isWeekPickerOpen.value) {
                    Dialog(
                        onDismissRequest = { isWeekPickerOpen.value = false }
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color.Transparent
                        ) {
                            Box(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(top = 30.dp)
                                        .fillMaxWidth()
                                        .background(
                                            color = CustomColors.DialogBackgroundColor,
                                            shape = RoundedCornerShape(percent = 10)
                                        )
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(20.dp)
                                    ) {
                                        Text(
                                            text = "Set Days",
                                            //fontFamily = CustomFonts.MinecraftFont,
                                            textAlign = TextAlign.Center,
                                            color = CustomColors.TextColor,
                                            modifier = Modifier.fillMaxWidth()
                                        )

                                        Row(
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Checkbox(
                                                checked = false,
                                                onCheckedChange = { isChecked -> print(isChecked) }
                                            )

                                            Text (
                                                text = "Monday"
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}