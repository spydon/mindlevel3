package net.mindlevel.api.endpoint;

import net.mindlevel.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginEndpoint {
    @POST("login")
    Call<String> login(@Body User user);

    @POST("logout")
    Call<Void> logout(@Body User user);
}