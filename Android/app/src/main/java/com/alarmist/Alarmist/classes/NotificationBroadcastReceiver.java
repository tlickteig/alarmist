package com.alarmist.Alarmist.classes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.alarmist.Alarmist.objects.Alarm;

public class NotificationBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String alarmString = intent.getStringExtra("alarmString");
        if (alarmString != null && alarmString != "") {
            Alarm alarm = AlarmSerializer.Companion.deserializeAlarm(alarmString);

            if (intent.getAction().equals("snooze")) {
                Utilities.Companion.snoozeAlarm(context, alarm);
            } else if (intent.getAction().equals("stop")) {
                Utilities.Companion.stopAlarm(context, alarm);
            }
        }
    }
}
