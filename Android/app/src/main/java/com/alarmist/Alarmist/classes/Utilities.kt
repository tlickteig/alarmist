package com.alarmist.Alarmist.classes

import android.app.Activity
import android.content.Context
import android.provider.Settings.Global.getString
import androidx.compose.runtime.mutableStateListOf
import com.alarmist.Alarmist.R
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Utilities {
    companion object {

        fun returnAllAlarms(activity: Activity): List<Alarm> {

            val sharedPref = activity?.getSharedPreferences(
                Constants.SHARED_PREFERENCES_FOR_ALL_ALARMS, Context.MODE_PRIVATE)
            var output: List<Alarm> = mutableListOf();
            val sharedPreferenceKeys = sharedPref!!.all.map { it.key }

            for (key in sharedPreferenceKeys) {
                val alarmString = sharedPref.getString(key, "")
                if (!alarmString.isNullOrBlank()) {
                    val alarm = AlarmSerialization.deserializeAlarm(alarmString!!)
                    output += listOf(alarm)
                }
            }
            return output;
        }

        fun saveOrUpdateAlarm(alarm: Alarm, activity: Activity) {

            val sharedPref = activity?.getSharedPreferences(
                Constants.SHARED_PREFERENCES_FOR_ALL_ALARMS, Context.MODE_PRIVATE) ?: return
            val serializedAlarm = AlarmSerialization.serializeAlarm(alarm);
            var id = alarm.id
            val key = "${Constants.ALARM_PREFIX_FOR_SHARED_PREFERENCES}_$id"

            with (sharedPref.edit()) {
                putString(key, serializedAlarm)
                apply()
            }
        }
    }
}

class AlarmSerialization {
    companion object {

        fun serializeAlarm(input: Alarm): String {
            return Json.encodeToString(input);
        }

        fun deserializeAlarm(input: String): Alarm {
            return Json.decodeFromString<Alarm>(input)
        }
    }
}