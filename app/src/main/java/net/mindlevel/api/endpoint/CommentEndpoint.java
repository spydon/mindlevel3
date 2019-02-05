package net.mindlevel.api.endpoint;

import net.mindlevel.model.Comment;
import net.mindlevel.model.Count;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CommentEndpoint {
    @GET("comment/{id}")
    Call<Comment> get(@Path("id") int id);

    @GET("comment/thread/{id}")
    Call<List<Comment>> getThread(@Path("id") int id);

    @GET("comment/thread/{id}/since/{timestamp}")
    Call<List<Comment>> getThreadSince(@Path("id") int id, @Path("timestamp") long timestamp);

    @GET("comment/thread/{id}/count")
    Call<Count> getCount(@Path("id") int id);

    @POST("comment")
    Call<Void> add(@Body Comment comment);
}