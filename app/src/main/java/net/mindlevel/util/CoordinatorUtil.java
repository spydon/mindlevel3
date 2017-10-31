package net.mindlevel.util;

import android.content.Context;
import android.content.Intent;

import net.mindlevel.CoordinatorActivity;
import net.mindlevel.model.Challenge;
import net.mindlevel.model.User;

public class CoordinatorUtil {
    public static void toUser(Context context, String username) {
        Intent coordinatorIntent = new Intent(context, CoordinatorActivity.class);
        coordinatorIntent.putExtra("username", username);
        context.startActivity(coordinatorIntent);
    }

    public static void toUser(Context context, User user) {
        Intent coordinatorIntent = new Intent(context, CoordinatorActivity.class);
        coordinatorIntent.putExtra("user", user);
        context.startActivity(coordinatorIntent);
    }

    public static void toFeed(Context context, String username) {
        Intent coordinatorIntent = new Intent(context, CoordinatorActivity.class);
        coordinatorIntent.putExtra("accomplishments_for_user", username);
        context.startActivity(coordinatorIntent);
    }

    public static void toFeed(Context context, Challenge Challenge) {
        Intent coordinatorIntent = new Intent(context, CoordinatorActivity.class);
        coordinatorIntent.putExtra("accomplishments_for_challenge", Challenge);
        context.startActivity(coordinatorIntent);
    }
}
