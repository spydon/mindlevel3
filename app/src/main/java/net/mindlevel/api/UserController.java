package net.mindlevel.api;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import net.mindlevel.R;
import net.mindlevel.api.endpoint.UserEndpoint;
import net.mindlevel.model.Login;
import net.mindlevel.model.User;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserController extends BackendService {

    private static UserEndpoint endpoint;

    public UserController(Context context) {
        super(context);
        endpoint = retrofit.create(UserEndpoint.class);
    }

    public void getAll(final ControllerCallback<List<User>> callback) {

        Call<List<User>> userCall = endpoint.getAll();

        userCall.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> usersResponse) {
                if(usersResponse.isSuccessful()) {
                    List<User> users = usersResponse.body();
                    callback.onPostExecute(true, users);
                } else {
                    callback.onPostExecute(false, null);
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                callback.onPostExecute(false, null);
                t.printStackTrace();
            }
        });
    }


    public void getUser(final String username, final ControllerCallback<User> callback) {

        Call<User> userCall = endpoint.get(username);

        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> userResponse) {
                if(userResponse.isSuccessful()) {
                    User user = userResponse.body();
                    callback.onPostExecute(true, user);
                    cacheUser(user);
                } else {
                    User user = readFromCache(username);
                    if(user == null) {
                        callback.onPostExecute(false, null);
                    } else {
                        callback.onPostExecute(true, user);
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                callback.onPostExecute(false, null);
                t.printStackTrace();
            }
        });
    }

    public void getUsernames(final ControllerCallback<String[]> callback) {

        Call<String[]> usernamesCall = endpoint.getUsernames();

        usernamesCall.enqueue(new Callback<String[]>() {
            @Override
            public void onResponse(Call<String[]> call, Response<String[]> userResponse) {
                if(userResponse.isSuccessful()) {
                    String[] usernames = userResponse.body();
                    callback.onPostExecute(true, usernames);
                } else {
                    callback.onPostExecute(false, null);
                }
            }

            @Override
            public void onFailure(Call<String[]> call, Throwable t) {
                callback.onPostExecute(false, null);
                t.printStackTrace();
            }
        });
    }

    public void update(final User user, final Uri path, final ControllerCallback<Void> callback) {
        InputStream is = null;
        MultipartBody.Part image = null;
        try {
            if(path != null && !TextUtils.isEmpty(path.getPath())) {
                is = context.getContentResolver().openInputStream(path);
                byte[] bytes = IOUtils.toByteArray(is);

                image = MultipartBody.Part.createFormData("image", null, RequestBody.create
                        (MediaType.parse("image/*"), bytes));
            }
            Call<Void> call = endpoint.update(user, image);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if(response.isSuccessful()) {
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
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(is != null) {
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
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    callback.onPostExecute(true, user.username);
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

    private void cacheUser(User user) {
        File outputDir = context.getFilesDir(); // TODO: getDataDir?
        String usersFilename = context.getString(R.string.users_file);
        File targetFile = new File(outputDir + "/" + usersFilename);
        String marshalled = user.toString();

        try {
            FileUtils.writeStringToFile(targetFile, marshalled, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private User readFromCache(String username) {
        File outputDir = context.getFilesDir(); // TODO: getDataDir?
        String missionsFilename = context.getString(R.string.missions_file);
        File targetFile = new File(outputDir + "/" + missionsFilename);
        String marshalled = "";
        try {
            marshalled = FileUtils.readFileToString(targetFile, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }

        User user = null;
        for(String u : marshalled.split("\n")) {
            User tmp = User.fromString(u, context);
            if(tmp.username.equals(username)) {
                user = tmp;
                break;
            }
        }
        return user;
    }
}
