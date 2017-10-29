package net.mindlevel.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

import net.mindlevel.R;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtil {

    public static boolean connectionCheck(Context context, View coordinator) {
        return connectionCheck(context, coordinator, true);
    }

    public static boolean connectionCheck(Context context, View coordinator, boolean showNotice) {
        if(!isConnected(context)) {
            if(showNotice) {
                String message = context.getString(R.string.error_network);
                showMessage(message, context, coordinator);
            }
            return false;
        } else {
            backendCheck(context, coordinator);
            return true;
        }
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public static void backendCheck(final Context context, final View coordinator) {
        AsyncTask<Void, Void, Boolean> backendCheckTask = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                Boolean backendAlive = true;
                try {
                    String backendAddress = context.getString(R.string.backend_address) + "ping";
                    HttpURLConnection urlc = (HttpURLConnection) (new URL(backendAddress).openConnection());
                    urlc.setRequestProperty("User-Agent", "mindlevel");
                    urlc.setRequestProperty("Connection", "close");
                    urlc.setConnectTimeout(3000);
                    urlc.connect();
                    if(urlc.getResponseCode() != 200) {
                        backendAlive = false;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    backendAlive = false;
                }
                return backendAlive;
            }

            @Override
            protected void onPostExecute(Boolean backendAlive) {
                if(!backendAlive) {
                    String message = context.getString(R.string.error_backend);
                    showMessage(message, context, coordinator);
                }
            }
        };
        backendCheckTask.execute();
    }

    private static void showMessage(String message, Context context, View coordinator) {
        if (coordinator != null) {
            Snackbar bar = Glassbar.make(coordinator, message, Snackbar.LENGTH_LONG);
            bar.show();
        } else {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }
}
