package net.mindlevel.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import net.mindlevel.R;
import net.mindlevel.api.endpoint.ChallengeEndpoint;
import net.mindlevel.model.Accomplishment;
import net.mindlevel.model.Category;
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
                    onFailure(call, new Throwable("Could not fetch challenges remotely"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Challenge>> call, @NonNull Throwable t) {
                List<Challenge> challenges = readFromCache();
                if (challenges.isEmpty()) {
                    callback.onPostExecute(false, null);
                    t.printStackTrace();
                    Log.w("mindlevel", "getAll challenges call failed");
                } else {
                    Log.i("mindlevel", "Got challenges from cache");
                    callback.onPostExecute(true, challenges);
                }
            }
        });
    }

    public void getCategories(final ControllerCallback<List<Category>> callback) {
        Call<List<Category>> call = endpoint.getCategories();
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(@NonNull Call<List<Category>> call, @NonNull Response<List<Category>> response) {
                if (response.isSuccessful()) {
                    callback.onPostExecute(true, response.body());
                    // TODO: Cache categories
                } else {
                    onFailure(call, new Throwable("Could not fetch challenges remotely"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Category>> call, @NonNull Throwable t) {
                // TODO: Read categories from cache
                t.printStackTrace();
                Log.w("mindlevel", "getCategories call failed");
                callback.onPostExecute(false, new ArrayList <Category>());
            }
        });
    }

    public void getChallengesByCategory(final Category category, final ControllerCallback<List<Challenge>> callback) {
        Call<List<Challenge>> call = endpoint.getChallengesByCategory(category.id);
        call.enqueue(new Callback<List<Challenge>>() {
            @Override
            public void onResponse(@NonNull Call<List<Challenge>> call, @NonNull Response<List<Challenge>> response) {
                if (response.isSuccessful()) {
                    callback.onPostExecute(true, response.body());
                    // TODO: Cache category challenges
                } else {
                    onFailure(call, new Throwable("Could not fetch challenges remotely"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Challenge>> call, @NonNull Throwable t) {
                // TODO: Read challenges from cache
                t.printStackTrace();
                Log.w("mindlevel", "getChallengesByCategory call failed");
                callback.onPostExecute(false, new ArrayList <Challenge>());
            }
        });
    }


    // TODO: Get from cached json file when call fails
    public void get(final int challengeId, final ControllerCallback<Challenge> callback) {
        Call<Challenge> call = endpoint.get(challengeId);
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

    public void getAccomplishments(final int challengeId, final ControllerCallback<List<Accomplishment>> callback) {
        Call<List<Accomplishment>> accomplishmentsCall = endpoint.getAccomplishments(challengeId);

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

    private void cacheChallenges(List<Challenge> challenges) {
        File outputDir = context.getFilesDir(); // TODO: getDataDir?
        String challengesFilename = context.getString(R.string.challenges_file);
        File targetFile = new File(outputDir + "/" + challengesFilename);
        ArrayList<String> marshallList = new ArrayList<>();
        for(Challenge m : challenges) {
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
        String challengesFilename = context.getString(R.string.challenges_file);
        File targetFile = new File(outputDir + "/" + challengesFilename);
        String marshalled;
        try {
            marshalled = FileUtils.readFileToString(targetFile, Charset.defaultCharset());
            for(String m : marshalled.split("\n")) {
                challenges.add(Challenge.fromString(m, context));
            }
        } catch (IOException e) {
            Log.w("mindlevel", "No challenges cached yet");
            return challenges;
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.w("mindlevel", "Wrong/corrupt format on cached file");
            return challenges;
        }

        return challenges;
    }
}
