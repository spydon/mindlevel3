package net.mindlevel.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.yalantis.ucrop.UCrop;

import net.mindlevel.R;
import net.mindlevel.api.ControllerCallback;
import net.mindlevel.api.UserController;
import net.mindlevel.impl.ProgressController;
import net.mindlevel.model.User;
import net.mindlevel.model.UserExtra;
import net.mindlevel.util.ImageUtil;
import net.mindlevel.util.KeyboardUtil;
import net.mindlevel.util.PreferencesUtil;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Edit a users details
 */
public class EditUserActivity extends AppCompatActivity {

    private UserController userController;
    private ImageUtil utils;
    private Uri path = null;

    // UI references.
    private ImageView imageView;
    private EditText passwordView1, passwordView2;
    private EditText descriptionView;
    private EditText emailView;
    private ProgressBar progressBar, progressEmail;
    private View editFormView, progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        userController = new UserController(getApplicationContext());

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_edit_profile);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        utils = new ImageUtil(this);
        FloatingActionButton choosePicture = findViewById(R.id.choose_picture);
        choosePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utils.dispatchGalleryIntent();
            }
        });

        FloatingActionButton takePicture = findViewById(R.id.take_picture);
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utils.dispatchTakePictureIntent();
            }
        });

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device does not have a camera
            takePicture.setVisibility(View.INVISIBLE);
        }

        imageView = findViewById(R.id.image);

        passwordView1 = findViewById(R.id.password1);
        passwordView2 = findViewById(R.id.password2);
        passwordView1.requestFocus();

        descriptionView = findViewById(R.id.description);
        emailView = findViewById(R.id.email);

        Button applyButton = findViewById(R.id.apply_button);
        applyButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptEdit();
            }
        });

        final ImageButton removeImageButton = findViewById(R.id.image_remove);
        removeImageButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                noImage(true);
            }
        });

        editFormView = findViewById(R.id.edit_form);
        progressBar = findViewById(R.id.progress_image);
        progressEmail = findViewById(R.id.progress_email);
        progressView = findViewById(R.id.progress);
        showProgress(false);

        User user = (User) getIntent().getSerializableExtra("user");
        descriptionView.setText(user.description);
        if (user.image != null && !user.image.isEmpty()) {
            String url = ImageUtil.getUrl(user.image);
            Glide.with(this)
                    .load(url)
                    .listener(new ProgressController(progressBar))
                    .into(imageView);
        }
        userController.getEmail(user.username, emailCallback);
    }

    /**
     * Attempts to edit the account specified by the login form.
     * If there are form errors (invalid password, missing fields, etc.), the
     * errors are presented and no actual edit attempt is made.
     */
    private void attemptEdit() {
        // Reset errors.
        passwordView1.setError(null);
        passwordView2.setError(null);

        // Store values at the time of the login attempt.
        String password1 = passwordView1.getText().toString();
        String password2 = passwordView2.getText().toString();
        String description = descriptionView.getText().toString();
        String email = emailView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!password1.equals(password2)) {
            passwordView1.setError(getString(R.string.error_invalid_password2));
            focusView = passwordView2;
            cancel = true;
        }

        if (!isPasswordValid(password1) && !password1.isEmpty()) {
            passwordView1.setError(getString(R.string.error_invalid_password));
            focusView = passwordView1;
            cancel = true;
        }

        if (!isEmailValid(email)) {
            emailView.setError("Not a valid email");
            focusView = emailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            KeyboardUtil.hideKeyboard(this);
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            String username = PreferencesUtil.getUsername(getApplicationContext());
            User user = new User(username, password1, description);
            UserExtra userExtra = new UserExtra(username, password1, email);
            userController.update(user, userExtra, path, editCallback);
        }
    }

    private boolean isEmailValid(String email) {
        return email.isEmpty() || (email.contains("@") && email.contains("."));
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4 || password.isEmpty();
    }

    private void showProgress(final boolean show) {
        fade(editFormView, !show);
        fade(progressView, show);
    }

    private void noImage(final boolean clear) {
        int longAnimTime = getResources().getInteger(android.R.integer.config_longAnimTime);
        final View container = findViewById(R.id.image_container);
        final Activity activity = this;
        Runnable endRunner = null;
        if (clear) {
            path = ImageUtil.uriFromDrawable(R.drawable.default_user);
            endRunner = new Runnable() {
                @Override
                public void run() {
                    Glide.with(activity)
                            .load(R.drawable.default_user)
                            .listener(new ProgressController(progressBar))
                            .into(imageView);
                    fade(container, true);
                }
            };
        }

        fade(container, !clear, longAnimTime, endRunner);
    }

    private void fade(final View view, final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        fade(view, show, shortAnimTime, null);
    }

    private void fade(final View view, final boolean show, final int animTime, final Runnable callback) {
        view.setVisibility(VISIBLE);
        view.animate().setDuration(animTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                view.setAlpha(show ? 0 : 1);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(show ? VISIBLE : GONE);
                if (callback != null) {
                    callback.run();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri maybePath = utils.handleImageResult(requestCode, resultCode, true, data, imageView, this);
        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK && maybePath != null) {
            path = maybePath;
            noImage(false);
        }
    }

    private ControllerCallback<String> emailCallback = new ControllerCallback<String>() {

        @Override
        public void onPostExecute(final Boolean success, final String email) {
            progressEmail.setVisibility(GONE);
            emailView.setEnabled(true);

            if (success) {
                emailView.setText(email);
            }
        }
    };

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
                setResult(RESULT_OK);
                finish();
            } else {
                passwordView1.setError(getString(R.string.error_incorrect_password));
                passwordView1.requestFocus();
            }
        }
    };
}

