package com.alarmist.Alarmist

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role.Companion.Button
import com.alarmist.Alarmist.classes.AlarmSchedule
import com.alarmist.Alarmist.classes.AlarmSerializer
import com.alarmist.Alarmist.classes.DataAccess
import com.alarmist.Alarmist.classes.Utilities

class AlarmGoingOff : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        wakeOnLock()
        super.onCreate(savedInstanceState)

        val extras = this.intent.extras
        val alarmString = extras!!.getString("alarmString")
        var alarm = AlarmSerializer.deserializeAlarm(alarmString!!)

        setContent {

            val context = LocalContext.current
            MaterialTheme {
                Scaffold { innerPadding ->
                    Column (
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        Button(
                            onClick = {
                                Utilities.stopPlayingRingtone()

                                if (alarm.scheduleMode == AlarmSchedule.ONE_TIME) {
                                    alarm.isEnabled = false
                                    DataAccess.saveOrUpdateAlarm(alarm, context)
                                }
                            }
                        ) {
                            Text("Stop")
                        }

                        Text("Alarm is going off")
                    }
                }
            }
        }
    }

    // // https://hackernoon.com/ditch-the-notification-and-show-an-activity-on-your-android-lock-screen-instead
    private fun wakeOnLock() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
    }
}