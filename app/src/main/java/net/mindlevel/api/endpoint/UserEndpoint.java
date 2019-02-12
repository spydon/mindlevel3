package net.mindlevel.api.endpoint;

import net.mindlevel.model.Accomplishment;
import net.mindlevel.model.Login;
import net.mindlevel.model.Notification;
import net.mindlevel.model.User;
import net.mindlevel.model.UserExtra;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface UserEndpoint {
    @GET("user")
    Call<List<User>> getAll();

    @GET("user/{user}")
    Call<User> get(@Path("user") String user);

    @GET("user/{user}/accomplishment")
    Call<List<Accomplishment>> getAccomplishments(@Path("user") String user);

    @GET("user/{user}/notification")
    Call<List<Notification>> getNotifications(@Path("user") String user);

    @DELETE("user/{user}/notification/{id}")
    Call<Void> deleteNotification(@Path("user") String user, @Path("id") int id);

    @GET("user/{user}/email")
    Call<String> getEmail(@Path("user") String user);

    @GET("user/highscore/{amount}")
    Call<List<User>> getHighscore(@Path("amount") int amount);

    @GET("user/usernames")
    Call<String[]> getUsernames();

    @Multipart
    @PUT("user/{user}")
    Call<Void> update(
            @Part("user") User user,
            @Part("extra") UserExtra userExtra,
            @Part MultipartBody.Part image
    );

    @POST("user")
    Call<Void> register(@Body Login user);
}
