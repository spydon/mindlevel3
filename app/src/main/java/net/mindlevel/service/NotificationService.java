package net.mindlevel.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import net.mindlevel.CoordinatorActivity;
import net.mindlevel.R;
import net.mindlevel.activity.AccomplishmentActivity;
import net.mindlevel.activity.ChallengeActivity;
import net.mindlevel.api.ControllerCallback;
import net.mindlevel.api.UserController;
import net.mindlevel.model.Notification;
import net.mindlevel.util.PreferencesUtil;
import net.mindlevel.model.Notification.Type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NotificationService extends JobIntentService {
    public static final int JOB_ID = 1016;
    public static final String ACTION_ID = "LOCAL ENQUEUE";
    public static final int INTERVAL_TIME = 1800*1000; // Check every half an hour
    private UserController userController;
    private Context context;

    public static void enqueueWork(Context context, Intent work) {
        long lastTimestamp = PreferencesUtil.getLastNotificationTime(context);
        long currentTimestamp = System.currentTimeMillis();
        if(lastTimestamp+INTERVAL_TIME-30 < currentTimestamp) {
            PreferencesUtil.setLastNotificationTime(context, currentTimestamp);
            enqueueWork(context, NotificationService.class, JOB_ID, work);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        enqueueWork(this, new Intent(ACTION_ID));
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        if(intent.getAction() != null && intent.getAction().equals(ACTION_ID)) {
            this.context = getApplicationContext();
            this.userController = new UserController(context);
            String username = PreferencesUtil.getUsername(context);
            userController.getNotifications(username, notificationsCallback);
        }
    }

    private ControllerCallback<List<Notification>> notificationsCallback = new ControllerCallback<List<Notification>>() {

        @Override
        public void onPostExecute(final Boolean success, final List<Notification> response) {
            if (success) {
                List<Notification> comments = new ArrayList <>();
                List<Notification> accomplishments = new ArrayList <>();
                List<Notification> challenges = new ArrayList <>();
                List<Notification> other = new ArrayList <>();

                List<List<Notification>> priority = new ArrayList <>();
                priority.add(comments);
                priority.add(other);
                priority.add(challenges);
                priority.add(accomplishments);

                for (Notification notification : response) {
                    Type type = notification.getType();
                    switch (type) {
                        case COMMENT:
                            comments.add(notification);
                            break;
                        case ACCOMPLISHMENT:
                            accomplishments.add(notification);
                            break;
                        case CHALLENGE:
                            challenges.add(notification);
                            break;
                        default:
                            other.add(notification);
                    }
                }

                for (List<Notification> notifications : priority) {
                    if (!notifications.isEmpty()) {
                        sendNotification(notifications);
                        break;
                    }
                }

            } else {
                // Handle?
            }
        }
    };

    private void sendNotification(List<Notification> notifications) {
        Context context = getApplicationContext();
        Comparator<Notification> byCreationTime = new Comparator <Notification>() {
            @Override
            public int compare(Notification n1, Notification n2) {
                // TODO: Check that this is the correct order, latest first
                return (int)((n1.created-1000) - (n2.created-1000));
            }
        };
        String channelId = context.getString(R.string.app_name);

        Collections.sort(notifications, byCreationTime);
        Notification notification = notifications.get(0);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channelId);
        int icon = R.drawable.logo;
        String targetIdName = "skip";
        Class<?> activityClass = CoordinatorActivity.class;
        switch (notification.getType()) {
            case ACCOMPLISHMENT:
                icon = R.drawable.accomplishment;
                activityClass = AccomplishmentActivity.class;
                targetIdName = "accomplishment_id";
                break;
            case CHALLENGE:
                icon = R.drawable.challenge;
                activityClass = ChallengeActivity.class;
                targetIdName = "challenge_id";
                break;
            case COMMENT:
                icon = R.drawable.logo;
                activityClass = AccomplishmentActivity.class;
                targetIdName = "accomplishment_id";
                break;
            case CHAT:
                icon = R.drawable.send;
                activityClass = CoordinatorActivity.class;
                targetIdName = "chat";
                break;
            case OTHER:
                // Default values are already filled in
                break;
        }

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        Intent current = new Intent(context, activityClass);
        current.putExtra(targetIdName, notification.targetId);
        stackBuilder.addParentStack(activityClass);
        stackBuilder.addNextIntent(current);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder
                .setSmallIcon(icon)
                .setContentTitle(notification.title)
                .setContentText(notification.description)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // notificationID allows you to update the notification later on.
        notificationManager.notify(notification.id, notificationBuilder.build());
        removeSimilarNotifications(notification, notifications);
    }

    private void removeSimilarNotifications(Notification notification, List<Notification> notifications) {
        String username = PreferencesUtil.getUsername(context);
        ControllerCallback emptyCallback = new ControllerCallback <Void>() {
            @Override
            public void onPostExecute(Boolean isSuccess, Void response) {
                // Don't care about the result, if it fails it will be deleted at a later point
            }
        };

        for (Notification other : notifications) {
            if (notification.targetId == other.targetId) {
                userController.deleteNotification(username, other.id, emptyCallback);
            }
        }
    }
}