package net.mindlevel.api;

import android.content.Context;

import net.mindlevel.api.endpoint.MissionEndpoint;
import net.mindlevel.model.Mission;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MissionController extends BackendService {

    private static MissionEndpoint endpoint;

    public MissionController(Context context) {
        super(context);
        endpoint = retrofit.create(MissionEndpoint.class);
    }

    public void get(final int missionId, final ControllerCallback<Mission> callback) {
        Call<Mission> call = endpoint.get(missionId);
        call.enqueue(new Callback<Mission>() {
            @Override
            public void onResponse(Call<Mission> call, Response<Mission> response) {
                if(response.isSuccessful()) {
                    callback.onPostExecute(true, response.body());
                } else {
                    callback.onPostExecute(false, null);
                }
            }

            @Override
            public void onFailure(Call<Mission> call, Throwable t) {
                callback.onPostExecute(false, null);
                t.printStackTrace();
            }
        });
    }
}
