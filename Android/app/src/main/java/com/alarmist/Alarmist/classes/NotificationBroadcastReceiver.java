package com.alarmist.Alarmist.classes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.alarmist.Alarmist.objects.Alarm;

public class NotificationBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Alarm alarm = Utilities.Companion.getCurrentAlarmNotification();
        if (intent.getAction().equals("snooze")) {
            Utilities.Companion.snoozeAlarm(context, alarm, 5);
        } else if (intent.getAction().equals("stop")) {
            Utilities.Companion.stopAlarm(context, alarm);
        }
    }
}
