package net.mindlevel.api;

import android.content.Context;
import android.content.SharedPreferences;
import net.mindlevel.api.endpoint.LoginEndpoint;
import net.mindlevel.model.Login;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginController extends BackendService {

    private static LoginEndpoint endpoint;

    private Context context;

    public LoginController(Context context) {
        super();
        this.endpoint = retrofit.create(LoginEndpoint.class);
        this.context = context;
    }


    public void login(final Login user, final ControllerCallback<String> callback) {

        Call<String> login = endpoint.login(user);

        login.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()) {
                    SharedPreferences sharedPreferences = context.getSharedPreferences("session", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username", user.username);
                    editor.putString("sessionId", response.body());
                    editor.apply();
                    callback.onPostExecute(true, response.body());
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
}
