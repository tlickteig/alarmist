package com.alarmist.Alarmist.classes

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.app.Notification
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_MUTABLE
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.startForegroundService
import com.alarmist.Alarmist.AlarmGoingOff
import com.alarmist.Alarmist.R
import com.alarmist.Alarmist.objects.Alarm


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

class NotificationHelper {
    companion object {
        // https://hackernoon.com/ditch-the-notification-and-show-an-activity-on-your-android-lock-screen-instead
        fun createAlarmGoingOffNotification(fullScreenIntent: Intent, context: Context,
                                                    alarm: Alarm): Notification {

            val fullScreenPendingIntent = PendingIntent.getActivity(context, 0, fullScreenIntent, FLAG_IMMUTABLE)
            var contentTitle = "Alarm is going off"
            val alarmString = AlarmSerializer.serializeAlarm(alarm)
            if (!alarm.name.isNullOrBlank()) {
                val alarmName = alarm.name
                contentTitle = "$alarmName is going off"
            }

            val snoozeIntent = Intent(context, NotificationBroadcastReceiver::class.java).apply { action = "snooze" }
            snoozeIntent.putExtra("alarmString", alarmString)
            val snoozePendingIntent = PendingIntent.getBroadcast(context, 0, snoozeIntent, FLAG_MUTABLE)

            val stopIntent = Intent(context, NotificationBroadcastReceiver::class.java).apply { action = "stop" }
            stopIntent.putExtra("alarmString", alarmString)
            val stopPendingIntent = PendingIntent.getBroadcast(context, 0, stopIntent, FLAG_MUTABLE)

            return NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
                .setContentTitle(contentTitle)
                .setContentText("Tap to snooze or shut off")
                .setAutoCancel(true)
                .setContentIntent(fullScreenPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .addAction(R.drawable.ic_launcher_foreground, "Snooze", snoozePendingIntent)
                .addAction(R.drawable.ic_launcher_foreground, "Stop", stopPendingIntent)
                .build()
        }

        fun areNotificationsEnabled(context: Context): Boolean {
            return NotificationManagerCompat.from(context).areNotificationsEnabled()
        }

        fun requestNotificationPermission(activity: Activity) {
            val permissions: Array<String> = arrayOf( "android.permission.POST_NOTIFICATIONS" )
            ActivityCompat.requestPermissions(activity, permissions, 1)
        }

        fun openNotificationSettings(context: Context) {
            var intent = Intent();
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("android.provider.extra.APP_PACKAGE", context.packageName)
            context.startActivity(intent)
        }
    }
}

class Utilities {
    companion object {
        private var ringtone: Ringtone? = null
        var currentAlarmNotification: Alarm = Alarm()

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

        fun setAlarmGoingOff(alarm: Alarm, context: Context) {
            try {
                var intent = Intent(
                    context, AlarmGoingOff::class.java
                )

                currentAlarmNotification = alarm
                val notification = NotificationHelper.createAlarmGoingOffNotification(intent, context, alarm)

                with (NotificationManagerCompat.from(context)) {
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        cancel(Constants.NOTIFICATION_GOING_OFF_ID)
                        notify(Constants.NOTIFICATION_GOING_OFF_ID, notification)
                    }
                }

                startPlayingRingtone(context)
            }
            catch (exception: Exception) {
                Log.e("Alarmist", "sfdsadf")
            }
        }

        fun snoozeAlarm(context: Context, alarm: Alarm, snoozeMinutes: Int) {
            NotificationManagerCompat.from(context).cancel(Constants.NOTIFICATION_GOING_OFF_ID)
            stopPlayingRingtone()
            alarm.snoozeMinutes = snoozeMinutes
            DataAccess.saveOrUpdateAlarm(alarm, context)
        }

        fun stopAlarm(context: Context, alarm: Alarm) {
            NotificationManagerCompat.from(context).cancel(Constants.NOTIFICATION_GOING_OFF_ID)
            stopPlayingRingtone()
            alarm.snoozeMinutes = 0

            if (alarm.scheduleMode == AlarmSchedule.ONE_TIME) {
                alarm.isEnabled = false
                DataAccess.saveOrUpdateAlarm(alarm, context)
            }
        }

        private fun startPlayingRingtone(context: Context) {
            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ringtone = RingtoneManager.getRingtone(context, uri)
            ringtone!!.play()
        }

        private fun stopPlayingRingtone() {
            if (ringtone != null) {
                ringtone!!.stop()
            }
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