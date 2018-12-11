package net.mindlevel.api.endpoint;

import net.mindlevel.model.Accomplishment;
import net.mindlevel.model.Category;
import net.mindlevel.model.Challenge;

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

public interface ChallengeEndpoint {
    @GET("challenge")
    Call<List<Challenge>> getAll();

    @GET("challenge/restricted")
    Call<List<Challenge>> getAllRestricted(); // Gives all challenges, but not with all content

    @Multipart
    @POST("challenge")
    Call<Void> add(
            @Part("challenge") Challenge challenge,
            @Part MultipartBody.Part image
    );

    @POST("challenge")
    Call<Void> add(@Body Challenge Challenge);

    @GET("challenge/category")
    Call<List<Category>> getCategories();

    @GET("challenge/category/{id}")
    Call<List<Challenge>> getChallengesByCategory(@Path("id") int id);

    @GET("challenge/{id}")
    Call<Challenge> get(@Path("id") int id);

    @GET("challenge/{id}/accomplishment")
    Call<List<Accomplishment>> getAccomplishments(@Path("id") int id);

    @GET("challenge/{range}")
    Call<List<Challenge>> get(@Path("range") String range);

    @Multipart
    @POST("challenge/{id}/image")
    Call<Void> addImage(
            @Path("id") int id,
            @Part("description") RequestBody description,
            @Part MultipartBody.Part file
    );

    @GET("challenge/latest")
    Call<List<Challenge>> getLatest();

    @GET("challenge/latest/{range]")
    Call<List<Challenge>> getLatest(@Path("range") String range);
}