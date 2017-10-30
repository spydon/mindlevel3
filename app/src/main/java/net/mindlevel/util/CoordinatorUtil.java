package net.mindlevel.util;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import net.mindlevel.CoordinatorActivity;
import net.mindlevel.R;
import net.mindlevel.model.Accomplishment;
import net.mindlevel.model.Challenge;
import net.mindlevel.model.User;

import java.util.ArrayList;
import java.util.List;

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
