package net.mindlevel.api;

import android.content.Context;

import net.mindlevel.api.endpoint.PingEndpoint;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PingController extends BackendService {

    private static PingEndpoint endpoint;

    public PingController(Context context) {
        super(context);
        endpoint = retrofit.create(PingEndpoint.class);
    }

    public void ping(final ControllerCallback<Void> callback) {

        Call<Void> pingCall = endpoint.ping();

        pingCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> pingResponse) {
                if (pingResponse.isSuccessful()) {
                    callback.onPostExecute(true, null);
                } else {
                    callback.onPostExecute(false, null);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onPostExecute(false, null);
                t.printStackTrace();
            }
        });
    }
}
