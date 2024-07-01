package com.alarmist.Alarmist.classes;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.alarmist.Alarmist.objects.Alarm;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class BackgroundProcessor extends Service {

    @Override
    public void onCreate() {
        try {
            startForegroundService();
        }
        catch (Exception e) {

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mainBackgroundThread();
                }
            }, 0, Constants.BACKGROUND_THREAD_INTERVAL_MS);

            startForegroundService();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startForegroundService() {
        if (Build.VERSION.SDK_INT >= 26) {
            final String channelId = "Alarmist Service";
            NotificationChannel channel = new NotificationChannel(
                    channelId, channelId, NotificationManager.IMPORTANCE_LOW
            );

            getSystemService(NotificationManager.class).createNotificationChannel(channel);
            Notification.Builder notification = new Notification.Builder(this, channelId).setContentText("Alarms are set");

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                startForeground(1001, notification.build());
            } else {
                startForeground(1001, notification.build(), ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK);
            }
        }
    }

    private void mainBackgroundThread() {
        Log.e("Service", "Service is running!");

        Context context = getApplicationContext();
        List<Alarm> alarms = DataAccess.Companion.returnAllAlarms(context);

        for (Alarm alarm : alarms) {
            if (alarm.isEnabled()) {
                if (shouldAlarmBeGoingOff(alarm)) {
                    Log.e("Service", "Alarm " + alarm.getId() + " is going off!");

                    if (alarm.getScheduleMode() == AlarmSchedule.ONE_TIME) {
                        alarm.setEnabled(false);
                        DataAccess.Companion.saveOrUpdateAlarm(alarm, context);
                    }
                }
            }
        }
    }

    private Boolean shouldAlarmBeGoingOff(Alarm alarm) {

        Boolean output = false;
        if (alarm.isEnabled()) {
            LocalDateTime localDateTime = LocalDateTime.now();
            if (alarm.getScheduleMode() == AlarmSchedule.ONE_TIME) {
                if (alarm.getTime().getHour() == localDateTime.getHour() &&
                        alarm.getTime().getMinute() == localDateTime.getMinute()) {
                    output = true;
                }
            } else if (alarm.getScheduleMode() == AlarmSchedule.SCHEDULED) {
                if (alarm.getDaysOfWeekString().contains(
                        localDateTime.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.US))) {
                    if (alarm.getTime().getHour() == localDateTime.getHour() &&
                            alarm.getTime().getMinute() == localDateTime.getMinute()) {
                        output = true;
                    }

                }
            } else if (alarm.getScheduleMode() == AlarmSchedule.SPECIFIC_DAYS) {
                for (LocalDate alarmDate : alarm.getSpecificDays()) {
                    if (alarmDate.getDayOfYear() == localDateTime.getDayOfYear()) {
                        if (alarm.getTime().getHour() == localDateTime.getHour() &&
                                alarm.getTime().getMinute() == localDateTime.getMinute()) {
                            output = true;
                        }
                    }
                }
            }
        }

        return output;
    }
}
