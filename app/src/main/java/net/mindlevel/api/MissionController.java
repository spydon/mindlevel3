package net.mindlevel.api;

import android.content.Context;
import android.text.TextUtils;

import net.mindlevel.R;
import net.mindlevel.api.endpoint.MissionEndpoint;
import net.mindlevel.model.Mission;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
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
                    callback.onPostExecute(true, response.body());
                    cacheMissions(response.body());
                } else {
                    List<Mission> missions = readFromCache();
                    if(missions.isEmpty()) {
                        callback.onPostExecute(false, null);
                    } else {
                        callback.onPostExecute(true, missions);
                    }
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
        String missionsFilename = context.getString(R.string.missions_file);
        File targetFile = new File(outputDir + "/" + missionsFilename);
        ArrayList<String> marshallList = new ArrayList<>();
        for(Mission m : missions) {
            marshallList.add(m.toString(context));
        }
        String marshalled = TextUtils.join("\n" , marshallList);

        try {
            FileUtils.writeStringToFile(targetFile, marshalled, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Mission> readFromCache() {
        File outputDir = context.getFilesDir(); // TODO: getDataDir?
        String missionsFilename = context.getString(R.string.missions_file);
        File targetFile = new File(outputDir + "/" + missionsFilename);
        String marshalled = "";
        try {
            marshalled = FileUtils.readFileToString(targetFile, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<Mission> missions = new ArrayList<>();
        for(String m : marshalled.split("\n")) {
            missions.add(Mission.fromString(m, context));
        }
        return missions;
    }
}
