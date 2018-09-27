package net.mindlevel.api.endpoint;

import net.mindlevel.model.Integration;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface IntegrationEndpoint {
    @GET("custom/{pass}")
    Call<Integration> get(@Path("pass") String pass);
}
