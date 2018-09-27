package net.mindlevel.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import net.mindlevel.api.endpoint.IntegrationEndpoint;
import net.mindlevel.model.Integration;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IntegrationController extends BackendService {

    private static IntegrationEndpoint endpoint;

    public IntegrationController(Context context) {
        super(context);
        endpoint = retrofit.create(IntegrationEndpoint.class);
    }

    public void get(String integration, final ControllerCallback<Integration> callback) {
        Call<Integration> integrationCall= endpoint.get(integration);

        integrationCall.enqueue(new Callback<Integration>() {
            @Override
            public void onResponse(@NonNull Call<Integration> call, @NonNull Response<Integration> integrationResponse) {
                if (integrationResponse.isSuccessful()) {
                    Integration integration = integrationResponse.body();
                    callback.onPostExecute(true, integration);
                } else {
                    callback.onPostExecute(false, null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Integration> call, @NonNull Throwable t) {
                callback.onPostExecute(false, null);
                t.printStackTrace();
                Log.w("mindlevel", "get integration failed");
            }
        });
    }
}
