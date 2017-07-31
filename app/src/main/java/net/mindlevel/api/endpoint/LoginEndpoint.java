package net.mindlevel.api.endpoint;

import net.mindlevel.model.Login;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginEndpoint {
    @POST("login")
    Call<String> login(@Body Login login);

    @POST("logout")
    Call<Void> logout(@Body Login login);
}