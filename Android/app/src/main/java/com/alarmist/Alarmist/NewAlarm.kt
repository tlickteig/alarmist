package com.alarmist.Alarmist

import android.app.Activity
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.alarmist.Alarmist.classes.AlarmSchedule
import com.alarmist.Alarmist.classes.AlarmSerializer
import com.alarmist.Alarmist.classes.CustomColors
import com.alarmist.Alarmist.classes.DataAccess
import com.alarmist.Alarmist.classes.DaysOfWeek
import com.alarmist.Alarmist.objects.Alarm
import com.commandiron.wheel_picker_compose.WheelTimePicker
import com.commandiron.wheel_picker_compose.core.TimeFormat
import com.commandiron.wheel_picker_compose.core.WheelPickerDefaults
import com.flexcode.multiselectcalendar.MultiSelectCalendar
import com.flexcode.multiselectcalendar.rememberMultiSelectCalendarState
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
class NewAlarm : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var isWeekPickerOpen = remember { mutableStateOf(false) }
            var isCalendarOpen = remember { mutableStateOf(false) }
            var isDeleteDialogOpen = remember { mutableStateOf(false) }

            val context = LocalContext.current
            val activity = context as Activity

            MaterialTheme {
                var initialAlarm = Alarm()
                val extras = this.intent.extras
                if (extras != null) {
                    var alarmString = extras!!.getString("alarm")
                    if (!alarmString.isNullOrBlank()) {
                        initialAlarm = AlarmSerializer.deserializeAlarm(alarmString)
                    }
                }

                var alarmName by remember { mutableStateOf(TextFieldValue(initialAlarm.name)) }
                var isSundayChecked by remember {
                    mutableStateOf(initialAlarm.daysOfWeek.contains(DaysOfWeek.SUNDAY)) }
                var isMondayChecked by remember {
                    mutableStateOf(initialAlarm.daysOfWeek.contains(DaysOfWeek.MONDAY)) }
                var isTuesdayChecked by remember {
                    mutableStateOf(initialAlarm.daysOfWeek.contains(DaysOfWeek.TUESDAY)) }
                var isWednesdayChecked by remember {
                    mutableStateOf(initialAlarm.daysOfWeek.contains(DaysOfWeek.WEDNESDAY)) }
                var isThursdayChecked by remember {
                    mutableStateOf(initialAlarm.daysOfWeek.contains(DaysOfWeek.THURSDAY)) }
                var isFridayChecked by remember {
                    mutableStateOf(initialAlarm.daysOfWeek.contains(DaysOfWeek.FRIDAY)) }
                var isSaturdayChecked by remember {
                    mutableStateOf(initialAlarm.daysOfWeek.contains(DaysOfWeek.SATURDAY)) }
                val multiSelectState = rememberMultiSelectCalendarState(
                    initialSelectedDates = initialAlarm.specificDays
                )
                var timeToGoOff by remember { mutableStateOf(LocalTime.MIDNIGHT) }
                var isAlarmEnabled by remember { mutableStateOf(initialAlarm.isEnabled) }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            colors = TopAppBarDefaults.mediumTopAppBarColors(
                                containerColor = CustomColors.TitleBarColor
                            ),
                            title = {
                                if (initialAlarm.id == 0) {
                                    Text("Create new Alarm")
                                }
                                else {
                                    Text("Edit Alarm")
                                }
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
                            ),
                            startTime = initialAlarm.time
                        ) {
                            snappedTime ->
                            timeToGoOff = snappedTime
                        }

                        Row {
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

                        fun processNewAlarm() {
                            var schedule = AlarmSchedule.ONE_TIME
                            var selectedDates = multiSelectState.selectedState.selected

                            if (isSundayChecked || isMondayChecked || isTuesdayChecked
                                || isWednesdayChecked || isThursdayChecked || isFridayChecked
                                || isSaturdayChecked) {

                                schedule = AlarmSchedule.SCHEDULED
                            } else if (selectedDates.isNotEmpty()) {
                                schedule = AlarmSchedule.SPECIFIC_DAYS
                            }

                            val selectedDays: HashSet<DaysOfWeek> = hashSetOf()
                            if (isSundayChecked) {
                                selectedDays.add(DaysOfWeek.SUNDAY)
                            }

                            if (isMondayChecked) {
                                selectedDays.add(DaysOfWeek.MONDAY)
                            }

                            if (isTuesdayChecked) {
                                selectedDays.add(DaysOfWeek.TUESDAY)
                            }

                            if (isWednesdayChecked) {
                                selectedDays.add(DaysOfWeek.WEDNESDAY)
                            }

                            if (isThursdayChecked) {
                                selectedDays.add(DaysOfWeek.THURSDAY)
                            }

                            if (isFridayChecked) {
                                selectedDays.add(DaysOfWeek.FRIDAY)
                            }

                            if (isSaturdayChecked) {
                                selectedDays.add(DaysOfWeek.SATURDAY)
                            }

                            var newId = initialAlarm.id
                            if (newId == 0) {
                                newId = DataAccess.returnAvailableAlarmId(activity)
                            }

                            var alarm = Alarm().apply {
                                name = alarmName.text
                                specificDays = selectedDates
                                scheduleMode = schedule
                                daysOfWeek = selectedDays
                                time = timeToGoOff
                                id = newId
                                isEnabled = isAlarmEnabled
                            }

                            DataAccess.saveOrUpdateAlarm(alarm, activity)
                            finish()
                        }

                        Button(
                            onClick = {
                                processNewAlarm()
                            }
                        ) {
                            Text("Save Alarm")
                        }

                        if (initialAlarm.id != 0) {
                            FilledTonalButton(
                                onClick = {
                                    isDeleteDialogOpen.value = true
                                }
                            ) {
                                Text("Delete Alarm")
                            }
                        }
                    }
                }

                if (isDeleteDialogOpen.value) {
                    Dialog(
                        onDismissRequest = { isDeleteDialogOpen.value = false }
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
                                        Text("Are you sure?")
                                        Button(
                                            onClick = {
                                                DataAccess.deleteAlarm(activity, initialAlarm.id)
                                                finish()
                                            }
                                        ) {
                                            Text("Yes")
                                        }

                                        FilledTonalButton(
                                            onClick = {
                                                isDeleteDialogOpen.value = false
                                            }
                                        ) {
                                            Text("No")
                                        }
                                    }
                                }
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

                                        MultiSelectCalendar(
                                            calendarState = multiSelectState
                                        )

                                        TextButton(
                                            onClick = {
                                                isCalendarOpen.value = false
                                            }
                                        ) {
                                            Text(
                                                text = "Done"
                                            )
                                        }
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

                                        TextButton(
                                            onClick = {
                                                isWeekPickerOpen.value = false
                                            }
                                        ) {
                                            Text(
                                                text = "Done"
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