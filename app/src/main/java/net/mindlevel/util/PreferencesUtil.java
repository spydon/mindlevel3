package net.mindlevel.util;

import android.content.Context;
import android.content.SharedPreferences;

import net.mindlevel.model.Login;

public class PreferencesUtil {
    public static String getUsername(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("session", Context.MODE_PRIVATE);
        return sharedPreferences.getString("username", "");
    }

    public static boolean isLoggedIn(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("session", Context.MODE_PRIVATE);
        return sharedPreferences.contains("username");
    }

    public static String getSessionId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("session", Context.MODE_PRIVATE);
        return sharedPreferences.getString("sessionId", "");
    }

    public static String getIntegration(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("integration", Context.MODE_PRIVATE);
        return sharedPreferences.getString("integration", "");
    }

    public static void setIntegration(Context context, String integration) {
        SharedPreferences pref = context.getSharedPreferences("integration", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("integration", integration);
        editor.apply();
    }

    public static Login getLogin(Context context) {
        SharedPreferences pref = context.getSharedPreferences("session", Context.MODE_PRIVATE);
        return new Login(pref.getString("username", ""), "", pref.getString("session", ""));
    }

    public static boolean getTutorialSeen(Context context) {
        SharedPreferences pref = context.getSharedPreferences("session", Context.MODE_PRIVATE);
        return pref.getBoolean("tutorial_seen", false);
    }

    public static void setTutorialSeen(Context context, boolean seen) {
        SharedPreferences pref = context.getSharedPreferences("session", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("tutorial_seen", seen);
        editor.apply();
    }

    // TODO: Swap for reactive programming instead
    public static boolean getHasUploaded(Context context) {
        SharedPreferences pref = context.getSharedPreferences("session", Context.MODE_PRIVATE);
        return pref.getBoolean("has_uploaded", false);
    }

    public static void setHasUploaded(Context context, boolean hasUploaded) {
        SharedPreferences pref = context.getSharedPreferences("session", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("has_uploaded", hasUploaded);
        editor.apply();
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
        editor.putString("username", username.toLowerCase());
        editor.putString("sessionId", sessionId);
        editor.apply();
    }
}
