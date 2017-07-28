package net.mindlevel.api;

import net.mindlevel.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BackendService {
    private Retrofit retrofit;

    public BackendService() {
        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.42.37:8080/")
                .addConverterFactory(GsonConverterFactory.create())
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
