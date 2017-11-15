package net.mindlevel.api.endpoint;

import net.mindlevel.model.Accomplishment;
import net.mindlevel.model.Contributors;
import net.mindlevel.model.Like;
import net.mindlevel.model.User;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface AccomplishmentEndpoint {
    @Multipart
    @POST("accomplishment")
    Call<Accomplishment> add(
            @Part("accomplishment") Accomplishment accomplishment,
            @Part("contributors") Contributors contributors,
            @Part MultipartBody.Part image
    );

    @GET("accomplishment/{id}")
    Call<Accomplishment> get(@Path("id") int id);

    @GET("accomplishment/{range}")
    Call<List<Accomplishment>> get(@Path("range") String range);

    @GET("accomplishment/{id}/contributor")
    Call<List<User>> getContributors(@Path("id") int id);

    @POST("accomplishment/{id}/contributor")
    Call<Void> addContributors(
            @Path("id") int id,
            @Body List<String> contributors
    );

    @GET("accomplishment/{id}/like")
    Call<Like> like(@Path("id") int id);

    @GET("accomplishment/latest")
    Call<List<Accomplishment>> getLatest();

    @GET("accomplishment/latest/{range}")
    Call<List<Accomplishment>> getLatest(@Path("range") String range);
}