package net.mindlevel.api.endpoint;

import net.mindlevel.model.Accomplishment;
import net.mindlevel.model.Mission;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface MissionEndpoint {
    @GET("mission")
    Call<List<Mission>> getAll();

    @POST("mission")
    Call<Void> add(@Body Mission mission);

    @GET("mission/{id}")
    Call<Mission> get(@Path("id") int id);

    @GET("mission/{id}/accomplishment")
    Call<List<Accomplishment>> getAccomplishments(@Path("id") int id);

    @GET("mission/{range}")
    Call<List<Mission>> get(@Path("range") String range);

    @Multipart
    @POST("mission/{id}/image")
    Call<Void> addImage(
            @Path("id") int id,
            @Part("description") RequestBody description,
            @Part MultipartBody.Part file
    );

    @GET("mission/latest")
    Call<List<Mission>> getLatest();

    @GET("mission/latest/{range]")
    Call<List<Mission>> getLatest(@Path("range") String range);
}