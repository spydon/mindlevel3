package net.mindlevel.api.endpoint;

import net.mindlevel.model.Accomplishment;

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

public interface AccomplishmentEndpoint {
    @POST("accomplishment")
    Call<Void> add(@Body Accomplishment accomplishment);

    @GET("accomplishment/{id}")
    Call<Accomplishment> get(@Path("id") int id);

    @GET("accomplishment/{range}")
    Call<List<Accomplishment>> get(@Path("range") String range);

    @Multipart
    @POST("accomplishment/{id}/image")
    Call<Void> addImage(
            @Path("id") int id,
            @Part("description") RequestBody description,
            @Part MultipartBody.Part file
    );

    @GET("accomplishment/{id}/contributor")
    Call<List<String>> getContributors(@Path("id") int id);

    @POST("accomplishment/{id}/contributor")
    Call<Void> addContributors(
            @Path("id") int id,
            @Body List<String> contributors
    );

    @GET("accomplishment/latest")
    Call<List<Accomplishment>> getLatest();

    @GET("accomplishment/latest/{range]")
    Call<List<Accomplishment>> getLatest(@Path("range") String range);
}