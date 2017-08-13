package net.mindlevel.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import net.mindlevel.R;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtil {
    public static boolean connectionCheck(Context context, View coordinator) {
        String message = "";
        boolean hasConnection = true;
        if(!isConnected(context)) {
            message = context.getString(R.string.error_network);
            hasConnection = false;
        } else if (!isBackendAvailable(context)) {
            message = context.getString(R.string.error_backend);
            hasConnection = false;
        }

        if(!hasConnection && coordinator != null) {
            Snackbar.make(coordinator, message, Snackbar.LENGTH_LONG).show();
        } else if(!hasConnection) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
        return hasConnection;
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public static boolean isBackendAvailable(Context context) {
        // TODO Make async and move to separate thread
        //try {
        //    String backendAddress = context.getString(R.string.backend_address);
        //    HttpURLConnection urlc = (HttpURLConnection) (new URL(backendAddress).openConnection());
        //    urlc.setRequestProperty("User-Agent", "mindlevel");
        //    urlc.setRequestProperty("Connection", "close");
        //    urlc.setConnectTimeout(1500);
        //    urlc.connect();
        //    return urlc.getResponseCode() == 200;
        //} catch (IOException e) {
        //    e.printStackTrace();
        //}
        return true;
    }
}
