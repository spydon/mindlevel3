package net.mindlevel.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static net.mindlevel.service.NotificationService.INTERVAL_TIME;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG_BOOT_BROADCAST_RECEIVER = "BOOT_BROADCAST_RECEIVER";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i(TAG_BOOT_BROADCAST_RECEIVER, action);
        startServiceByAlarm(context);
    }

    private void startServiceByAlarm(Context context) {
        Intent notificationService = new Intent(context, NotificationService.class);
        PendingIntent pendingIntent =
                PendingIntent.getService(context, 0, notificationService, PendingIntent.FLAG_UPDATE_CURRENT);

        long startTime = System.currentTimeMillis();
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startTime, INTERVAL_TIME, pendingIntent);
    }
}