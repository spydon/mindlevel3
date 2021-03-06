package net.mindlevel.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import net.mindlevel.api.endpoint.LoginEndpoint;
import net.mindlevel.model.Login;
import net.mindlevel.util.PreferencesUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginController extends BackendService {

    private static LoginEndpoint endpoint;

    public LoginController(Context context) {
        super(context);
        endpoint = retrofit.create(LoginEndpoint.class);
    }

    public void login(final Login user, final ControllerCallback<String> callback) {

        Call<String> login = endpoint.login(user);

        login.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    String sessionId = response.body();
                    PreferencesUtil.setSessionState(user.username, sessionId, context);
                    callback.onPostExecute(true, sessionId);
                } else {
                    callback.onPostExecute(false, null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                callback.onPostExecute(false, null);
                t.printStackTrace();
                Log.w("mindlevel", "login call failed");
            }
        });
    }

    public void logout(final Login user, final ControllerCallback<Void> callback) {

        Call<Void> logout = endpoint.logout(user);

        logout.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (callback == null){
                    return;
                }

                if (response.isSuccessful()) {
                    PreferencesUtil.clearSession(context);
                    callback.onPostExecute(true, null);
                } else {
                    callback.onPostExecute(false, null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                t.printStackTrace();
                Log.w("mindlevel", "logout call failed");
                if (callback == null){
                    return;
                }
                callback.onPostExecute(false, null);
            }
        });
    }

}
