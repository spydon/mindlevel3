package net.mindlevel.api.endpoint;

import retrofit2.Call;
import retrofit2.http.GET;

public interface PingEndpoint {
    @GET("ping")
    Call<Void> ping();
}
