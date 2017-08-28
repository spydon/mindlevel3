package net.mindlevel.api;

import android.content.Context;

import net.mindlevel.api.endpoint.MissionEndpoint;
import net.mindlevel.model.Mission;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MissionController extends BackendService {

    private static MissionEndpoint endpoint;

    public MissionController(Context context) {
        super(context);
        endpoint = retrofit.create(MissionEndpoint.class);
    }

    public void getAll(final ControllerCallback<List<Mission>> callback) {
        Call<List<Mission>> call = endpoint.getAll();
        call.enqueue(new Callback<List<Mission>>() {
            @Override
            public void onResponse(Call<List<Mission>> call, Response<List<Mission>> response) {
                if(response.isSuccessful()) {
                    // TODO: Cache missions
                    callback.onPostExecute(true, response.body());
                    cacheMissions(response.body());
                } else {
                    // TODO: send back missions that are cached
                    // Handle when cached missions don't exist
                    callback.onPostExecute(false, null);
                }
            }

            @Override
            public void onFailure(Call<List<Mission>> call, Throwable t) {
                // TODO: send back missions that are cached
                // Handle when cached missions don't exist
                callback.onPostExecute(false, null);
                t.printStackTrace();
            }
        });
    }

    // TODO: Get from cached json file when call fails
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

    private void cacheMissions(List<Mission> missions) {
        File outputDir = context.getFilesDir(); // TODO: getDataDir?
        File targetFile = new File(outputDir + "/missions.txt");
        try {
            FileUtils.writeStringToFile(targetFile, missions.toString(), Charset.defaultCharset());
        } catch (IOException e) {
            // Not that important if caching was not successful
        }
    }
}
