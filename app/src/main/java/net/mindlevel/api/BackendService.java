package net.mindlevel.api;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class BackendService {
    protected static Retrofit retrofit;
    protected Context context;

    public BackendService(Context context) {
        this.context = context;
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.42.244:8080/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    protected void addSessionState(String username, String sessionId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("session", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("sessionId", sessionId);
        editor.apply();
    }
}
