package com.alarmist.Alarmist.classes

import android.app.Activity
import android.app.ActivityManager
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startForegroundService
import com.alarmist.Alarmist.objects.Alarm
import java.util.concurrent.TimeUnit

class DataAccess {
    companion object {
        fun returnAllAlarms(context: Context): List<Alarm> {

            var output: List<Alarm> = mutableListOf();
            val sharedPref = context?.getSharedPreferences(
                Constants.SHARED_PREFERENCES_FOR_ALL_ALARMS, Context.MODE_PRIVATE
            )
            val sharedPreferenceKeys = sharedPref!!.all.map { it.key }

            for (key in sharedPreferenceKeys) {
                val alarmString = sharedPref.getString(key, "")
                if (!alarmString.isNullOrBlank()) {
                    val alarm = AlarmSerializer.deserializeAlarm(alarmString!!)
                    output += listOf(alarm)
                }
            }

            return output;
        }

        fun saveOrUpdateAlarm(alarm: Alarm, context: Context) {

            val sharedPref = context?.getSharedPreferences(
                Constants.SHARED_PREFERENCES_FOR_ALL_ALARMS, Context.MODE_PRIVATE) ?: return
            val serializedAlarm = AlarmSerializer.serializeAlarm(alarm);
            var id = alarm.id
            val key = "${Constants.ALARM_PREFIX_FOR_SHARED_PREFERENCES}_$id"

            with (sharedPref.edit()) {
                putString(key, serializedAlarm)
                apply()
            }
        }

        fun returnAvailableAlarmId(context: Context): Int {

            val sharedPref = context?.getSharedPreferences(
                Constants.SHARED_PREFERENCES_FOR_ALL_ALARMS, Context.MODE_PRIVATE)
            val sharedPreferenceKeys = sharedPref!!.all.map { it.key }
            var maxAlarmId = 0
            for (key in sharedPreferenceKeys) {
                val alarmString = sharedPref.getString(key, "")
                if (!alarmString.isNullOrBlank()) {
                    val alarm = AlarmSerializer.deserializeAlarm(alarmString!!)
                    if (alarm.id > maxAlarmId) {
                        maxAlarmId = alarm.id
                    }
                }
            }

            return maxAlarmId + 1
        }

        fun deleteAlarm(context: Context, alarmId: Int) {
            val sharedPref = context?.getSharedPreferences(
                Constants.SHARED_PREFERENCES_FOR_ALL_ALARMS, Context.MODE_PRIVATE) ?: return
            val key = "${Constants.ALARM_PREFIX_FOR_SHARED_PREFERENCES}_$alarmId"

            with (sharedPref.edit()) {
                remove(key)
                apply()
            }
        }

        fun returnAllCategories(context: Context): List<String> {
            val alarms = returnAllAlarms(context)
            var output: List<String> = mutableListOf()

            for (alarm in alarms) {
                var category = alarm.category
                if (!category.isNullOrBlank()) {
                    if (!output.contains(category)) {
                        output += listOf(category)
                    }
                }
            }

            return output
        }
    }
}

class Utilities {
    companion object {
        private fun areAnyAlarmsEnabled(context: Context): Boolean {
            var output = false
            var allAlarms = DataAccess.returnAllAlarms(context)
            for (alarm in allAlarms) {
                if (alarm.isEnabled) {
                    output = true
                }
            }

            return output
        }

        fun setBackgroundThread(context: Context) {
            if (areAnyAlarmsEnabled(context)) {
                if (!isBackgroundThreadAlreadyRunning(context)) {
                    var serviceIntent = Intent(context, BackgroundProcessor::class.java)
                    startForegroundService(context, serviceIntent)
                }
            }
        }

        private fun isBackgroundThreadAlreadyRunning(context: Context): Boolean {
            var activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (service in activityManager.getRunningServices(Int.MAX_VALUE)) {
                if (BackgroundProcessor::class.qualifiedName.equals(service.service.className)) {
                    return true
                }
            }

            return false
        }
    }
}

class ExtensionMethods {
    companion object {
        fun AlarmSchedule.labelValue(): String {
            var output = "One Time"
            if (this == AlarmSchedule.SCHEDULED) {
                output = "Scheduled"
            } else if (this == AlarmSchedule.SPECIFIC_DAYS) {
                output = "Specific Days"
            }

            return output
        }

        fun String.alarmScheduleEnumValue(): AlarmSchedule {
            var output = AlarmSchedule.ONE_TIME
            if (this == "Scheduled") {
                output = AlarmSchedule.SCHEDULED
            } else if (this == "Specific Days") {
                output = AlarmSchedule.SPECIFIC_DAYS
            }

            return output
        }
    }
}