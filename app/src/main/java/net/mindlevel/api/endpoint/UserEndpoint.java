package net.mindlevel.api.endpoint;

import net.mindlevel.model.Login;
import net.mindlevel.model.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserEndpoint {
    @GET("user/{user}")
    Call<User> getUser(@Path("user") String user);

    @PUT("user/{user}")
    Call<Boolean> updateUser(@Path("user") User user);

    @POST("user/{user}")
    Call<Void> register(@Path("user") Login user);
}
