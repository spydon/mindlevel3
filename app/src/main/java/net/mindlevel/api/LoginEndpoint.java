package net.mindlevel.api;

import net.mindlevel.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface LoginEndpoint {
  @POST("login")
  Call<String> login(@Body User user);

  @POST("logout")
  Call<Void> logout(@Body User user);
}