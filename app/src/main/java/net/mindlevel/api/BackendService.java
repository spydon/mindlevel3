package net.mindlevel.api;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.mindlevel.R;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class BackendService {
    static Retrofit retrofit;
    protected Context context;
    private SharedPreferences sharedPreferences;

    BackendService(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences("session", Context.MODE_PRIVATE);
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Interceptor sessionHeaderInterceptor = new Interceptor() {
            @Override public Response intercept(Chain chain) throws IOException {
                String sessionId = sharedPreferences.getString("sessionId", "");
                Request request = chain.request().newBuilder().addHeader("X-Session", sessionId).build();
                return chain.proceed(request);
            }
        };

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(sessionHeaderInterceptor);

        retrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.backend_address))
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    void addSessionState(String username, String sessionId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("sessionId", sessionId);
        editor.apply();
    }
}