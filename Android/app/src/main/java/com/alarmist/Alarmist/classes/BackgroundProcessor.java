package com.alarmist.Alarmist.classes;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class BackgroundProcessor extends Service {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            while (true) {
                                Log.e("Service", "Service is running!");
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
            ).start();

            final String channelId = "Alarmist Service";
            NotificationChannel channel = new NotificationChannel(
                    channelId, channelId, NotificationManager.IMPORTANCE_LOW
            );

            getSystemService(NotificationManager.class).createNotificationChannel(channel);
            Notification.Builder notification = new Notification.Builder(this, channelId).setContentText("Alarms are set");

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                startForeground(1001, notification.build());
            } else {
                startForeground(1001, notification.build(), ServiceInfo.FOREGROUND_SERVICE_TYPE_SHORT_SERVICE);
            }
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
}