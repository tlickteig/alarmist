package com.alarmist.Alarmist.composables

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alarmist.Alarmist.NewAlarm
import com.alarmist.Alarmist.classes.AlarmSerializer
import com.alarmist.Alarmist.classes.DataAccess
import com.alarmist.Alarmist.classes.Utilities
import com.alarmist.Alarmist.objects.Alarm

@Composable
fun AlarmItem(alarm: Alarm) {
    val context = LocalContext.current
    val activity = LocalContext.current as Activity

    Column (
        modifier = Modifier.clickable {
            val intent = Intent(
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
                    checked = alarm.isEnabled,
                    onCheckedChange = {
                        alarm.isEnabled = it
                        DataAccess.saveOrUpdateAlarm(alarm, activity)
                        Utilities.setBackgroundThread(context)
                    }
                )
            }
        }

        if (alarm.subText.isNotBlank()) {
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