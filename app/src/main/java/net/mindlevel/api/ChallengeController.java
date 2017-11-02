package net.mindlevel.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import net.mindlevel.R;
import net.mindlevel.api.endpoint.ChallengeEndpoint;
import net.mindlevel.model.Accomplishment;
import net.mindlevel.model.Challenge;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChallengeController extends BackendService {

    private static ChallengeEndpoint endpoint;

    public ChallengeController(Context context) {
        super(context);
        endpoint = retrofit.create(ChallengeEndpoint.class);
    }

    public void getAll(final ControllerCallback<List<Challenge>> callback) {
        Call<List<Challenge>> call = endpoint.getAll();
        call.enqueue(new Callback<List<Challenge>>() {
            @Override
            public void onResponse(@NonNull Call<List<Challenge>> call, @NonNull Response<List<Challenge>> response) {
                if (response.isSuccessful()) {
                    callback.onPostExecute(true, response.body());
                    cacheChallenges(response.body());
                } else {
                    onFailure(call, new Throwable("Could not fetch Challenges remotely"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Challenge>> call, @NonNull Throwable t) {
                List<Challenge> Challenges = readFromCache();
                if (Challenges.isEmpty()) {
                    callback.onPostExecute(false, null);
                } else {
                    callback.onPostExecute(true, Challenges);
                }
                t.printStackTrace();
                Log.w("mindlevel", "getAll challanges call failed");
            }
        });
    }

    // TODO: Get from cached json file when call fails
    public void get(final int ChallengeId, final ControllerCallback<Challenge> callback) {
        Call<Challenge> call = endpoint.get(ChallengeId);
        call.enqueue(new Callback<Challenge>() {
            @Override
            public void onResponse(@NonNull Call<Challenge> call, @NonNull Response<Challenge> response) {
                if (response.isSuccessful()) {
                    callback.onPostExecute(true, response.body());
                } else {
                    callback.onPostExecute(false, null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Challenge> call, @NonNull Throwable t) {
                callback.onPostExecute(false, null);
                t.printStackTrace();
                Log.w("mindlevel", "get challange call failed");
            }
        });
    }

    public void getAccomplishments(final int ChallengeId, final ControllerCallback<List<Accomplishment>> callback) {
        Call<List<Accomplishment>> accomplishmentsCall = endpoint.getAccomplishments(ChallengeId);

        accomplishmentsCall.enqueue(new Callback<List<Accomplishment>>() {
            @Override
            public void onResponse(@NonNull Call<List<Accomplishment>> call,
                                   @NonNull Response<List<Accomplishment>> accomplishmentsResponse) {
                if (accomplishmentsResponse.isSuccessful()) {
                    List<Accomplishment> accomplishments = accomplishmentsResponse.body();
                    callback.onPostExecute(true, accomplishments);
                } else {
                    callback.onPostExecute(false, null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Accomplishment>> call, @NonNull Throwable t) {
                callback.onPostExecute(false, null);
                t.printStackTrace();
                Log.w("mindlevel", "get accomplishments for challenge call failed");
            }
        });
    }

    private void cacheChallenges(List<Challenge> Challenges) {
        File outputDir = context.getFilesDir(); // TODO: getDataDir?
        String ChallengesFilename = context.getString(R.string.challenges_file);
        File targetFile = new File(outputDir + "/" + ChallengesFilename);
        ArrayList<String> marshallList = new ArrayList<>();
        for(Challenge m : Challenges) {
            marshallList.add(m.toString(context));
        }
        String marshalled = TextUtils.join("\n" , marshallList);

        try {
            FileUtils.writeStringToFile(targetFile, marshalled, Charset.defaultCharset());
        } catch (IOException e) {
            Log.e("mindlevel", "Could not write to challenge cache");
        }
    }

    private List<Challenge> readFromCache() {
        File outputDir = context.getFilesDir(); // TODO: getDataDir?
        ArrayList<Challenge> challenges = new ArrayList<>();
        String ChallengesFilename = context.getString(R.string.challenges_file);
        File targetFile = new File(outputDir + "/" + ChallengesFilename);
        String marshalled;
        try {
            marshalled = FileUtils.readFileToString(targetFile, Charset.defaultCharset());
        } catch (IOException e) {
            Log.w("mindlevel", "No challenges cached yet");
            return challenges;
        }

        for(String m : marshalled.split("\n")) {
            challenges.add(Challenge.fromString(m, context));
        }
        return challenges;
    }
}
