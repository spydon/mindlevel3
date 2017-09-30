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
import net.mindlevel.model.Mission;
import net.mindlevel.model.User;

import java.util.ArrayList;
import java.util.List;

public class CoordinatorUtil {
    public static void toUser(Context context, String username) {
        Intent mainIntent = new Intent(context, CoordinatorActivity.class);
        mainIntent.putExtra("username", username);
        context.startActivity(mainIntent);
    }

    public static void toUser(Context context, User user) {
        Intent mainIntent = new Intent(context, CoordinatorActivity.class);
        mainIntent.putExtra("user", user);
        context.startActivity(mainIntent);
    }

    public static void toFeed(Context context, String username) {
        Intent mainIntent = new Intent(context, CoordinatorActivity.class);
        mainIntent.putExtra("accomplishments_for_user", username);
        context.startActivity(mainIntent);
    }

    public static void toFeed(Context context, Mission mission) {
        Intent mainIntent = new Intent(context, CoordinatorActivity.class);
        mainIntent.putExtra("accomplishments_for_mission", mission);
        context.startActivity(mainIntent);
    }
}
