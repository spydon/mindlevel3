package net.mindlevel.util;

import android.content.Context;
import android.content.Intent;

import net.mindlevel.CoordinatorActivity;
import net.mindlevel.model.Category;
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

    public static void toFeed(Context context, Challenge challenge) {
        Intent coordinatorIntent = new Intent(context, CoordinatorActivity.class);
        coordinatorIntent.putExtra("accomplishments_for_challenge", challenge);
        context.startActivity(coordinatorIntent);
    }

    public static void toChallenges(Context context, Category category) {
        Intent coordinatorIntent = new Intent(context, CoordinatorActivity.class);
        coordinatorIntent.putExtra("challenges_by_category", category);
        context.startActivity(coordinatorIntent);
    }

    public static void toChat(Context context, Category category) {
        Intent coordinatorIntent = new Intent(context, CoordinatorActivity.class);
        coordinatorIntent.putExtra("chat", category);
        context.startActivity(coordinatorIntent);
    }

    public static void toLogout(Context context) {
        Intent coordinatorIntent = new Intent(context, CoordinatorActivity.class);
        coordinatorIntent.putExtra("logout", true);
        context.startActivity(coordinatorIntent);
    }
}
