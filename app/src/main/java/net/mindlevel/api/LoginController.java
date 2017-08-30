package net.mindlevel.api;

import android.content.Context;
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
        this.endpoint = retrofit.create(LoginEndpoint.class);
    }

    public void login(final Login user, final ControllerCallback<String> callback) {

        Call<String> login = endpoint.login(user);

        login.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()) {
                    String sessionId = response.body();
                    PreferencesUtil.setSessionState(user.username, sessionId, context);
                    callback.onPostExecute(true, sessionId);
                } else {
                    callback.onPostExecute(false, null);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callback.onPostExecute(false, null);
                t.printStackTrace();
            }
        });
    }

    public void logout(final Login user, final ControllerCallback<Void> callback) {

        Call<Void> logout = endpoint.logout(user);

        logout.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    PreferencesUtil.setSessionState(null, null, context);
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
    }

}
