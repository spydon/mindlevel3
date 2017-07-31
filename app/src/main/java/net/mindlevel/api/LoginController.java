package net.mindlevel.api;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.transition.ViewPropertyTransition;

import net.mindlevel.ProgressBarController;
import net.mindlevel.R;
import net.mindlevel.api.endpoint.LoginEndpoint;
import net.mindlevel.api.endpoint.UserEndpoint;
import net.mindlevel.model.Login;
import net.mindlevel.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginController extends BackendService {

    private static LoginEndpoint endpoint;
    private static View view;

    private Context context;

    public LoginController(Context context) {
        super();
        this.endpoint = retrofit.create(LoginEndpoint.class);
        this.context = context;
    }


    public void login(final Login user, final ControllerCallback callback) {

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
                    callback.onPostExecute(true);
                } else {
                    callback.onPostExecute(false);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callback.onPostExecute(false);
                t.printStackTrace();
            }
        });
    }
}
