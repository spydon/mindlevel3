package net.mindlevel.api;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import net.mindlevel.R;
import net.mindlevel.api.endpoint.UserEndpoint;
import net.mindlevel.model.Accomplishment;
import net.mindlevel.model.Login;
import net.mindlevel.model.User;
import net.mindlevel.model.UserExtra;
import net.mindlevel.util.PreferencesUtil;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserController extends BackendService {

    private static UserEndpoint endpoint;
    private final int HIGHSCORE_AMOUNT = 20;

    public UserController(Context context) {
        super(context);
        endpoint = retrofit.create(UserEndpoint.class);
    }

    public void getAll(final ControllerCallback<List<User>> callback) {
        Call<List<User>> userCall = endpoint.getAll();

        userCall.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(@NonNull Call<List<User>> call, @NonNull Response<List<User>> usersResponse) {
                if (usersResponse.isSuccessful()) {
                    List<User> users = usersResponse.body();
                    callback.onPostExecute(true, users);
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

     public void getHighscore(final ControllerCallback<List<User>> callback) {
        Call<List<User>> highscoreCall = endpoint.getHighscore(HIGHSCORE_AMOUNT);

        highscoreCall.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(@NonNull Call<List<User>> call, @NonNull Response<List<User>> usersResponse) {
                if (usersResponse.isSuccessful()) {
                    List<User> users = usersResponse.body();
                    callback.onPostExecute(true, users);
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

    public void getUser(final String username, final ControllerCallback<User> callback) {
        Call<User> userCall = endpoint.get(username);

        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> userResponse) {
                if (userResponse.isSuccessful()) {
                    User user = userResponse.body();
                    callback.onPostExecute(true, user);
                    cacheUser(user);
                } else {
                    onFailure(call, new Throwable("User not fetched"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                User user = readFromCache(username);
                if (user == null) {
                    t.printStackTrace();
                    callback.onPostExecute(false, null);
                } else {
                    callback.onPostExecute(true, user);
                }
            }
        });
    }

    public void getAccomplishments(final String username, final ControllerCallback<List<Accomplishment>> callback) {
        Call<List<Accomplishment>> accomplishmentsCall = endpoint.getAccomplishments(username);

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
            }
        });
    }

    public void getUsernames(final ControllerCallback<String[]> callback) {
        Call<String[]> usernamesCall = endpoint.getUsernames();

        usernamesCall.enqueue(new Callback<String[]>() {
            @Override
            public void onResponse(@NonNull Call<String[]> call, @NonNull Response<String[]> userResponse) {
                if (userResponse.isSuccessful()) {
                    String[] usernames = userResponse.body();
                    callback.onPostExecute(true, usernames);
                } else {
                    callback.onPostExecute(false, null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<String[]> call, @NonNull Throwable t) {
                callback.onPostExecute(false, null);
                t.printStackTrace();
            }
        });
    }

    public void getEmail(final String username, final ControllerCallback<String> callback) {
        Call<String> emailCall = endpoint.getEmail(username);

        emailCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> emailResponse) {
                if (emailResponse.isSuccessful()) {
                    callback.onPostExecute(true, emailResponse.body());
                } else {
                    callback.onPostExecute(false, null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                callback.onPostExecute(false, null);
                t.printStackTrace();
            }
        });
    }

    public void update(final User user, final UserExtra userExtra, final Uri path,
                       final ControllerCallback<Void> callback) {
        InputStream is = null;
        MultipartBody.Part image = null;
        try {
            if (path != null && !TextUtils.isEmpty(path.getPath())) {
                is = context.getContentResolver().openInputStream(path);
                byte[] bytes = is != null ? IOUtils.toByteArray(is) : new byte[0];

                image = MultipartBody.Part.createFormData("image", null, RequestBody.create
                        (MediaType.parse("image/*"), bytes));
            }
            Call<Void> call = endpoint.update(user, userExtra, image);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        callback.onPostExecute(true, null);
                    } else {
                        callback.onPostExecute(false, null);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    callback.onPostExecute(false, null);
                    t.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void register(final Login user, final ControllerCallback<String> callback) {
        Call<Void> register = endpoint.register(user);

        register.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onPostExecute(true, user.username);
                } else {
                    callback.onPostExecute(false, null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                callback.onPostExecute(false, null);
                t.printStackTrace();
            }
        });
    }

    private void cacheUser(User user) {
        if (!PreferencesUtil.getUsername(context).equals(user.username)) {
            return;
        }
        File outputDir = context.getFilesDir(); // TODO: getDataDir?
        String usersFilename = context.getString(R.string.users_file);
        File targetFile = new File(outputDir + "/" + usersFilename);
        String marshalled = user.toString(context) + "\n";

        try {
            FileUtils.writeStringToFile(targetFile, marshalled, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private User readFromCache(String username) {
        if (!PreferencesUtil.getUsername(context).equals(username)) {
            return null;
        }
        File outputDir = context.getFilesDir(); // TODO: getDataDir?
        String missionsFilename = context.getString(R.string.users_file);
        File targetFile = new File(outputDir + "/" + missionsFilename);
        String marshalled = "";
        try {
            marshalled = FileUtils.readFileToString(targetFile, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }

        User user = null;
        for(String u : marshalled.split("\n")) {
            if (u.contains(context.getString(R.string.field_delim))) {
                User tmp = User.fromString(u, context);
                if (tmp.username.equals(username)) {
                    user = tmp;
                    break;
                }
            }
        }
        return user;
    }
}
