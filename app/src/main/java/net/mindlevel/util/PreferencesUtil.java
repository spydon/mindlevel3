package net.mindlevel.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import net.mindlevel.R;
import net.mindlevel.model.Login;

public class PreferencesUtil {
    public static String getUsername(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("session", Context.MODE_PRIVATE);
        return sharedPreferences.getString("username", "");
    }

    public static String getSessionId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("session", Context.MODE_PRIVATE);
        return sharedPreferences.getString("sessionId", "");
    }

    public static Login getLogin(Context context) {
        SharedPreferences pref = context.getSharedPreferences("session", Context.MODE_PRIVATE);
        Login login = new Login(pref.getString("username", ""), "", pref.getString("session", ""));
        return login;
    }

    public static void clearSession(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("session", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public static void setSessionState(String username, String sessionId, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("session", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("sessionId", sessionId);
        editor.apply();
    }
}
