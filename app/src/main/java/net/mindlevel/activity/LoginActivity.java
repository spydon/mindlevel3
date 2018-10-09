package net.mindlevel.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import net.mindlevel.R;
import net.mindlevel.api.ControllerCallback;
import net.mindlevel.api.IntegrationController;
import net.mindlevel.api.LoginController;
import net.mindlevel.api.UserController;
import net.mindlevel.impl.Glassbar;
import net.mindlevel.model.Integration;
import net.mindlevel.model.Login;
import net.mindlevel.util.KeyboardUtil;
import net.mindlevel.util.NetworkUtil;
import net.mindlevel.util.PreferencesUtil;

import java.util.Random;

public class LoginActivity extends AppCompatActivity {

    private LoginController loginController;
    private UserController userController;

    // UI references.
    private LoginActivity activity;
    private View coordinatorLayout;
    private EditText usernameView;
    private EditText passwordView;
    private View progressView;
    private View loginFormView;
    private TextInputLayout outerPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        activity = this;
        coordinatorLayout = findViewById(R.id.login_outer);
        loginFormView = findViewById(R.id.login_inner_form);
        final ScrollView scrollView = findViewById(R.id.login_form);
        loginController = new LoginController(loginFormView.getContext());
        userController = new UserController(loginFormView.getContext());

        // Set up the login form.
        usernameView = findViewById(R.id.username);

        passwordView = findViewById(R.id.password);
        passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE) {
                    attemptLogin(false);
                    return true;
                }
                return false;
            }
        });

        passwordView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    scrollView.smoothScrollTo(scrollView.getScrollX(), scrollView.getScrollY()+200);
                }
            }
        });

        outerPasswordView = findViewById(R.id.password_outer);

        View tosView = findViewById(R.id.terms);
        tosView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent termsIntent = new Intent(getApplicationContext(), TermsActivity.class);
                startActivity(termsIntent);
            }
        });

        View privacyView = findViewById(R.id.privacy);
        privacyView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent privacyIntent = new Intent(getApplicationContext(), PrivacyActivity.class);
                startActivity(privacyIntent);
            }
        });

        passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    attemptLogin(false);
                    return true;
                }
                return false;
            }
        });

        Button signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin(false);
            }
        });

        Button registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin(true);
            }
        });

        ImageView logoView = findViewById(R.id.logo);
        logoView.setOnClickListener(new OnClickListener() {
            final int magicNumber = 6;
            int clicks = 0;
            @Override
            public void onClick(View view) {
                clicks++;
                if (clicks == magicNumber) {
                    Intent integrationIntent = new Intent(activity, IntegrationActivity.class);
                    startActivity(integrationIntent);
                }
                clicks = clicks % magicNumber;
            }
        });

        progressView = findViewById(R.id.progress);
        TextView tip = findViewById(R.id.tip);
        tip.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setRandomTip();
            }
        });

        setRandomTip();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    private void setRandomTip() {
        TextView tipView = findViewById(R.id.tip);
        String[] tips = {
                getString(R.string.tip_like),
                getString(R.string.tip_contributor),
                getString(R.string.tip_swipe),
                getString(R.string.tip_share),
                getString(R.string.tip_new),
                getString(R.string.tip_points)
        };
        String current = tipView.getText().toString();
        int index = new Random().nextInt(tips.length);
        String tip = getString(R.string.tip, tips[index]);
        if (tip.equals(current)) {
            tip = getString(R.string.tip, tips[(index+1)%tips.length]);
        }
        tipView.setText(tip);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin(boolean isNewUser) {
        KeyboardUtil.hideKeyboard(this);
        if (!NetworkUtil.connectionCheck(getApplicationContext(), coordinatorLayout)) {
            return;
        }
        // Reset errors.
        usernameView.setError(null);
        outerPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = usernameView.getText().toString();
        String password = passwordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            outerPasswordView.setError(getString(R.string.error_invalid_password));
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
                outerPasswordView.setError(getString(R.string.error_incorrect_password));
                passwordView.requestFocus();
            }
        }
    };

    private ControllerCallback<String> registerCallback = new ControllerCallback<String>() {

        @Override
        public void onPostExecute(final Boolean success, final String response) {
            showProgress(false);

            if (success) {
                Glassbar.make(coordinatorLayout, response, Snackbar.LENGTH_LONG).show();
                usernameView.setEnabled(false);
                passwordView.setEnabled(false);
                attemptLogin(false);
            } else {
                outerPasswordView.setError(response);
                passwordView.requestFocus();
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        menu.findItem(R.id.sign_out_menu).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
	        case R.id.about_menu:
	            Intent aboutIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutIntent);
                return true;
            case R.id.facebook_menu:
                Intent facebookIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/mindlvl/"));
                startActivity(facebookIntent);
                return true;
            case R.id.tutorial_menu:
                Intent tutorialIntent = new Intent(this, TutorialActivity.class);
                startActivity(tutorialIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}

