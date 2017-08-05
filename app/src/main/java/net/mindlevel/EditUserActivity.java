package net.mindlevel;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.mindlevel.api.ControllerCallback;
import net.mindlevel.api.LoginController;
import net.mindlevel.api.UserController;
import net.mindlevel.model.Login;
import net.mindlevel.model.User;

import java.io.File;

import static net.mindlevel.ImageUtil.PICK_IMAGE;
import static net.mindlevel.ImageUtil.REQUEST_IMAGE_CAPTURE;

/**
 * A login screen that offers login via username/password.
 */
public class EditUserActivity extends AppCompatActivity {

    private UserController userController;
    private ImageUtil utils;
    private Uri path = null;

    // UI references.
    private EditText passwordView1;
    private EditText passwordView2;
    private EditText descriptionView;
    private View progressView;
    private View editFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        View innerView = findViewById(R.id.inner_login_form);
        userController = new UserController(innerView.getContext());
        utils = new ImageUtil(this);

        passwordView1 = (EditText) findViewById(R.id.password1);
        passwordView2 = (EditText) findViewById(R.id.password2);

        descriptionView = (EditText) findViewById(R.id.description);

        Button applyButton = (Button) findViewById(R.id.apply_button);
        applyButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        editFormView = findViewById(R.id.edit_form);
        progressView = findViewById(R.id.edit_progress);
    }

    /**
     * Attempts to edit the account specified by the login form.
     * If there are form errors (invalid password, missing fields, etc.), the
     * errors are presented and no actual edit attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        passwordView1.setError(null);
        passwordView2.setError(null);

        // Store values at the time of the login attempt.
        String password1 = passwordView1.getText().toString();
        String password2 = passwordView2.getText().toString();
        String description = descriptionView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!password1.equals(password2)) {
            passwordView1.setError(getString(R.string.error_invalid_password2));
            focusView = passwordView2;
            cancel = true;
        }

        if (isPasswordValid(password1)) {
            passwordView1.setError(getString(R.string.error_invalid_password));
            focusView = passwordView2;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            SharedPreferences sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
            String username = sharedPreferences.getString("username", "");
            User user = new User(username, password1, description);
            userController.update(user, path, editCallback);
        }
    }

    private boolean isEmailValid(String email) {
        return email.length() > 4;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        editFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        editFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                editFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        progressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri path = null;
        if(resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                path = Uri.fromFile(new File(utils.getPhotoPath()));
            } else if (requestCode == PICK_IMAGE) {
                if (data == null) {
                    // TODO: Display an error
                    return;
                }
                path = data.getData();
            }
            ImageView imageView = (ImageView)findViewById(R.id.image);
            utils.setImage(path, imageView);
        }
    }

    private ControllerCallback<Void> editCallback = new ControllerCallback<Void>() {

        @Override
        public void onPostExecute(final Boolean success, final Void nothing) {
            showProgress(false);

            if (success) {
                Context context = getApplicationContext();
                String text = getString(R.string.successful_edit);
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                finish();
            } else {
                passwordView1.setError(getString(R.string.error_incorrect_password));
                passwordView1.requestFocus();
            }
        }
    };
}

