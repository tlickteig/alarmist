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
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DatePicker
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
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.core.text.util.LocalePreferences.FirstDayOfWeek.Days
import com.alarmist.Alarmist.classes.Alarm
import com.alarmist.Alarmist.classes.AlarmSchedule
import com.alarmist.Alarmist.classes.CustomColors
import com.alarmist.Alarmist.classes.DaysOfWeek
import com.commandiron.wheel_picker_compose.WheelTimePicker
import com.commandiron.wheel_picker_compose.core.TimeFormat
import com.commandiron.wheel_picker_compose.core.WheelPickerDefaults
import com.flexcode.multiselectcalendar.MultiSelectCalendar
import com.flexcode.multiselectcalendar.rememberMultiSelectCalendarState

@OptIn(ExperimentalMaterial3Api::class)
class NewAlarm : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var isWeekPickerOpen = remember { mutableStateOf(false) }
            var isCalendarOpen = remember { mutableStateOf(false) }

            MaterialTheme {
                var alarmName by remember { mutableStateOf(TextFieldValue("")) }
                var scheduleMode by remember { mutableStateOf(AlarmSchedule.SCHEDULED) }

                var isSundayChecked by remember { mutableStateOf(false) }
                var isMondayChecked by remember { mutableStateOf(false) }
                var isTuesdayChecked by remember { mutableStateOf(false) }
                var isWednesdayChecked by remember { mutableStateOf(false) }
                var isThursdayChecked by remember { mutableStateOf(false) }
                var isFridayChecked by remember { mutableStateOf(false) }
                var isSaturdayChecked by remember { mutableStateOf(false) }
                val multiSelectState = rememberMultiSelectCalendarState(
                    initialSelectedDates = emptyList()
                )

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
                                onClick = { isCalendarOpen.value = true }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CalendarMonth,
                                    contentDescription = "Choose dates",
                                    tint = CustomColors.TextButtonColor
                                )
                            }
                        }
                    }
                }

                if (isCalendarOpen.value) {
                    Dialog(
                        onDismissRequest = { isCalendarOpen.value = false }
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
                                        .padding(top = 15.dp)
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
                                            textAlign = TextAlign.Center,
                                            color = CustomColors.TextColor,
                                            modifier = Modifier.fillMaxWidth()
                                        )

                                        /*DatePicker(
                                            state = selectedDate,
                                            title = { },
                                            showModeToggle = false,
                                        )*/

                                        MultiSelectCalendar(

                                        )
                                    }
                                }
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
                                            textAlign = TextAlign.Center,
                                            color = CustomColors.TextColor,
                                            modifier = Modifier.fillMaxWidth()
                                        )

                                        Row(
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Checkbox(
                                                checked = isSundayChecked,
                                                onCheckedChange = { isChecked ->
                                                    isSundayChecked = isChecked
                                                },
                                                colors = CheckboxDefaults.colors(
                                                    checkmarkColor = CustomColors.BackgroundColor,
                                                    uncheckedColor = CustomColors.TextColor,
                                                    checkedColor = CustomColors.TextColor
                                                )
                                            )

                                            Text (
                                                text = "Sunday",
                                                color = CustomColors.TextColor
                                            )
                                        }

                                        Row(
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Checkbox(
                                                checked = isMondayChecked,
                                                onCheckedChange = { isChecked ->
                                                    isMondayChecked = isChecked
                                                },
                                                colors = CheckboxDefaults.colors(
                                                    checkmarkColor = CustomColors.BackgroundColor,
                                                    uncheckedColor = CustomColors.TextColor,
                                                    checkedColor = CustomColors.TextColor
                                                )
                                            )

                                            Text (
                                                text = "Monday",
                                                color = CustomColors.TextColor
                                            )
                                        }

                                        Row(
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Checkbox(
                                                checked = isTuesdayChecked,
                                                onCheckedChange = { isChecked ->
                                                    isTuesdayChecked = isChecked
                                                },
                                                colors = CheckboxDefaults.colors(
                                                    checkmarkColor = CustomColors.BackgroundColor,
                                                    uncheckedColor = CustomColors.TextColor,
                                                    checkedColor = CustomColors.TextColor
                                                )
                                            )

                                            Text (
                                                text = "Tuesday",
                                                color = CustomColors.TextColor
                                            )
                                        }

                                        Row(
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Checkbox(
                                                checked = isWednesdayChecked,
                                                onCheckedChange = { isChecked ->
                                                    isWednesdayChecked = isChecked
                                                },
                                                colors = CheckboxDefaults.colors(
                                                    checkmarkColor = CustomColors.BackgroundColor,
                                                    uncheckedColor = CustomColors.TextColor,
                                                    checkedColor = CustomColors.TextColor
                                                )
                                            )

                                            Text (
                                                text = "Wednesday",
                                                color = CustomColors.TextColor
                                            )
                                        }

                                        Row(
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Checkbox(
                                                checked = isThursdayChecked,
                                                onCheckedChange = { isChecked ->
                                                    isThursdayChecked = isChecked
                                                },
                                                colors = CheckboxDefaults.colors(
                                                    checkmarkColor = CustomColors.BackgroundColor,
                                                    uncheckedColor = CustomColors.TextColor,
                                                    checkedColor = CustomColors.TextColor
                                                )
                                            )

                                            Text (
                                                text = "Thursday",
                                                color = CustomColors.TextColor
                                            )
                                        }

                                        Row(
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Checkbox(
                                                checked = isFridayChecked,
                                                onCheckedChange = { isChecked ->
                                                    isFridayChecked = isChecked
                                                },
                                                colors = CheckboxDefaults.colors(
                                                    checkmarkColor = CustomColors.BackgroundColor,
                                                    uncheckedColor = CustomColors.TextColor,
                                                    checkedColor = CustomColors.TextColor
                                                )
                                            )

                                            Text (
                                                text = "Friday",
                                                color = CustomColors.TextColor
                                            )
                                        }

                                        Row(
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Checkbox(
                                                checked = isSaturdayChecked,
                                                onCheckedChange = { isChecked ->
                                                    isSaturdayChecked = isChecked
                                                },
                                                colors = CheckboxDefaults.colors(
                                                    checkmarkColor = CustomColors.BackgroundColor,
                                                    uncheckedColor = CustomColors.TextColor,
                                                    checkedColor = CustomColors.TextColor
                                                )
                                            )

                                            Text (
                                                text = "Saturday",
                                                color = CustomColors.TextColor
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