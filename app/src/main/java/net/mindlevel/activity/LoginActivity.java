package net.mindlevel.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import net.mindlevel.R;
import net.mindlevel.api.ControllerCallback;
import net.mindlevel.api.LoginController;
import net.mindlevel.api.UserController;
import net.mindlevel.model.Login;
import net.mindlevel.util.KeyboardUtil;
import net.mindlevel.util.NetworkUtil;

import java.util.Random;

import static java.security.AccessController.getContext;

public class LoginActivity extends AppCompatActivity {

    private LoginController loginController;
    private UserController userController;

    // UI references.
    private View coordinatorLayout;
    private EditText usernameView;
    private EditText passwordView;
    private View progressView;
    private View loginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        coordinatorLayout = findViewById(R.id.login_outer);
        View innerView = findViewById(R.id.login_inner_form);
        loginController = new LoginController(innerView.getContext());
        userController = new UserController(innerView.getContext());

        // Set up the login form.
        usernameView = (EditText) findViewById(R.id.username);

        passwordView = (EditText) findViewById(R.id.password);
        passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin(false);
                    return true;
                }
                return false;
            }
        });

        View tosView = findViewById(R.id.terms);
        tosView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent termsIntent = new Intent(getApplicationContext(), TermsActivity.class);
                startActivity(termsIntent);
            }
        });

        Button signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin(false);
            }
        });

        Button registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin(true);
            }
        });

        loginFormView = findViewById(R.id.login_form);
        progressView = findViewById(R.id.progress);
        setRandomTip();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void setRandomTip() {
        TextView random = (TextView) findViewById(R.id.tip);
        String[] tips = {
                getString(R.string.tip_like),
                getString(R.string.tip_contributor),
                getString(R.string.tip_points)
        };
        String tip = tips[new Random().nextInt(tips.length)];
        random.setText(getString(R.string.tip, tip));
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin(boolean isNewUser) {
        KeyboardUtil.hideKeyboard(this);
        if(!NetworkUtil.connectionCheck(getApplicationContext(), coordinatorLayout)) {
            return;
        }
        // Reset errors.
        usernameView.setError(null);
        passwordView.setError(null);

        // Store values at the time of the login attempt.
        String username = usernameView.getText().toString();
        String password = passwordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordView.setError(getString(R.string.error_invalid_password));
            focusView = passwordView;
            cancel = true;
        }

        // Check for a valid username address.
        if (TextUtils.isEmpty(username)) {
            usernameView.setError(getString(R.string.error_field_required));
            focusView = usernameView;
            cancel = true;
        } else if (!isUsernameValid(username)) {
            usernameView.setError(getString(R.string.error_invalid_username));
            focusView = usernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else if (!isNewUser) {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            Login login = new Login(username, password);
            loginController.login(login, loginCallback);
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            Login login = new Login(username, password);
            userController.register(login, registerCallback);
        }
    }

    private boolean isUsernameValid(String email) {
        return email.length() > 3;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        loginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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

    private ControllerCallback<String> loginCallback = new ControllerCallback<String>() {

        @Override
        public void onPostExecute(final Boolean success, final String response) {
            showProgress(false);

            if (success) {
                finish();
            } else {
                passwordView.setError(getString(R.string.error_incorrect_password));
                passwordView.requestFocus();
            }
        }
    };

    private ControllerCallback<String> registerCallback = new ControllerCallback<String>() {

        @Override
        public void onPostExecute(final Boolean success, final String username) {
            showProgress(false);

            if (success) {
                String registrationText = getString(R.string.successful_registration, username);
                Snackbar.make(coordinatorLayout, registrationText, Snackbar.LENGTH_LONG).show();
            } else {
                passwordView.setError(getString(R.string.error_incorrect_password));
                passwordView.requestFocus();
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
	        case R.id.about_menu:
	            Intent aboutIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}

