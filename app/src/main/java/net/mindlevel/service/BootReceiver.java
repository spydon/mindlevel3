package net.mindlevel.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG_BOOT_BROADCAST_RECEIVER = "BOOT_BROADCAST_RECEIVER";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.e(TAG_BOOT_BROADCAST_RECEIVER, action);
        startServiceByAlarm(context);
    }

    private void startServiceByAlarm(Context context) {
        Intent notificationService = new Intent(context, NotificationService.class);
        PendingIntent pendingIntent =
                PendingIntent.getService(context, 0, notificationService, PendingIntent.FLAG_UPDATE_CURRENT);

        long startTime = System.currentTimeMillis();
        long intervalTime = 1800*1000; // Check every half an hour
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startTime, intervalTime, pendingIntent);
    }
}