package net.mindlevel.api;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.mindlevel.R;
import net.mindlevel.activity.LoginActivity;
import net.mindlevel.util.NetworkUtil;
import net.mindlevel.util.PreferencesUtil;

import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

abstract class BackendService {
    static Retrofit retrofit;
    protected Context context;

    BackendService(final Context context) {
        this.context = context;
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Interceptor sessionHeaderInterceptor = new Interceptor() {
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                String sessionId = PreferencesUtil.getSessionId(context);
                Request request = chain.request().newBuilder().addHeader("X-Session", sessionId).build();
                return chain.proceed(request);
            }
        };

        Interceptor errorInterceptor = new Interceptor() {
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                // Handle no network on client side
                if (!NetworkUtil.isConnected(context)) {
                    throw new SocketException("No network");
                }

                // Handle old session token
                Response response = chain.proceed(chain.request());
                int code = response.code();
                switch (code) {
                    case 401:
                    case 403:
                        Context baseContext = context.getApplicationContext();
                        PreferencesUtil.clearSession(baseContext);
                        Intent loginIntent = new Intent(baseContext, LoginActivity.class);
                        baseContext.startActivity(loginIntent);
                        break;
                }
                return response;
            }
        };

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(30, TimeUnit.SECONDS);
        httpClient.readTimeout(30, TimeUnit.SECONDS);
        httpClient.writeTimeout(120, TimeUnit.SECONDS);
        httpClient.addInterceptor(errorInterceptor);
        httpClient.addInterceptor(sessionHeaderInterceptor);

        retrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.backend_address))
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }
}
