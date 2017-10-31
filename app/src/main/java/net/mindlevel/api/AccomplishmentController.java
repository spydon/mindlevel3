package net.mindlevel.api;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import net.mindlevel.api.endpoint.AccomplishmentEndpoint;
import net.mindlevel.model.Accomplishment;
import net.mindlevel.model.Contributors;
import net.mindlevel.model.Like;
import net.mindlevel.model.User;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccomplishmentController extends BackendService {

    private static AccomplishmentEndpoint endpoint;
    public static int PAGE_SIZE = 20;

    public AccomplishmentController(Context context) {
        super(context);
        endpoint = retrofit.create(AccomplishmentEndpoint.class);
    }

    private byte[] compressImage(Uri path) throws IOException {
        InputStream is = context.getContentResolver().openInputStream(path);

        File outputDir = context.getCacheDir();
        File targetFile = File.createTempFile("mindlevel", ".jpg", outputDir);

        byte[] bytes;
        if (is != null) {
            FileUtils.copyInputStreamToFile(is, targetFile);
            File compressed = new Compressor(context).compressToFile(targetFile);
            bytes = FileUtils.readFileToByteArray(compressed);
        } else {
            bytes = new byte[0];
        }

        return bytes;
    }

    public void add(final Accomplishment accomplishment,
                    final Set<String> contributors,
                    final Uri path,
                    final ControllerCallback<Accomplishment> callback) {
        try {
            if (path != null && !TextUtils.isEmpty(path.getPath())) {
                byte[] bytes =  compressImage(path);

                MultipartBody.Part image = MultipartBody.Part.createFormData("image", null, RequestBody.create
                        (MediaType.parse("image/*"), bytes));

                // For some reason the implicit conversion from List gives a string instead of a list
                Call<Accomplishment> call = endpoint.add(accomplishment, new Contributors(contributors), image);
                call.enqueue(new Callback<Accomplishment>() {
                    @Override
                    public void onResponse(@NonNull Call<Accomplishment> call, @NonNull Response<Accomplishment> response) {
                        if (response.isSuccessful()) {
                            callback.onPostExecute(true, response.body());
                        } else {
                            callback.onPostExecute(false, null);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Accomplishment> call, @NonNull Throwable t) {
                        callback.onPostExecute(false, null);
                        t.printStackTrace();
                    }
                });
            } else {
                callback.onPostExecute(false, null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getContributors(int id, final ControllerCallback<List<User>> callback) {
        Call<List<User>> call = endpoint.getContributors(id);
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(@NonNull Call<List<User>> call, @NonNull Response<List<User>> response) {
                if (response.isSuccessful()) {
                    callback.onPostExecute(true, response.body());
                } else {
                    callback.onPostExecute(false, null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<User>> call, @NonNull Throwable t) {
                callback.onPostExecute(false, null);
                t.printStackTrace();
            }
        });
    }

    public void like(int id, final ControllerCallback<Like> callback) {
        Call<Like> call = endpoint.like(id);
        call.enqueue(new Callback<Like>() {
            @Override
            public void onResponse(@NonNull Call<Like> call, @NonNull Response<Like> response) {
                if (response.isSuccessful()) {
                    callback.onPostExecute(true, response.body());
                } else {
                    callback.onPostExecute(false, null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Like> call, @NonNull Throwable t) {
                callback.onPostExecute(false, null);
                t.printStackTrace();
            }
        });
    }

    public void getLatest(final ControllerCallback<List<Accomplishment>> callback) {
        Call<List<Accomplishment>> call = endpoint.getLatest();
        call.enqueue(new Callback<List<Accomplishment>>() {
            @Override
            public void onResponse(@NonNull Call<List<Accomplishment>> call, @NonNull Response<List<Accomplishment>> response) {
                if (response.isSuccessful()) {
                    callback.onPostExecute(true, response.body());
                } else {
                    callback.onPostExecute(false, null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Accomplishment>> call, @NonNull Throwable t) {
                callback.onPostExecute(false, null);
                t.printStackTrace();
            }
        });
    }

    public void getLatest(String range, final ControllerCallback<List<Accomplishment>> callback) {
        Call<List<Accomplishment>> call = endpoint.getLatest(range);
        call.enqueue(new Callback<List<Accomplishment>>() {
            @Override
            public void onResponse(@NonNull Call<List<Accomplishment>> call, @NonNull Response<List<Accomplishment>> response) {
                if (response.isSuccessful()) {
                    callback.onPostExecute(true, response.body());
                } else {
                    callback.onPostExecute(false, null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Accomplishment>> call, @NonNull Throwable t) {
                callback.onPostExecute(false, null);
                t.printStackTrace();
            }
        });
    }
}
