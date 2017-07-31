package net.mindlevel.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.mindlevel.api.endpoint.UserEndpoint;
import net.mindlevel.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class BackendService {
    protected static Retrofit retrofit;

    public BackendService() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.20.106:8080/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public void getUser(String username) {
        UserEndpoint service = retrofit.create(UserEndpoint.class);

        Call<User> user = service.getUser(username);

        user.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> user) {
                System.out.println(user.body().username);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

}
