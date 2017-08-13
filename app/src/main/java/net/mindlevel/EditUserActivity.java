package net.mindlevel;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import net.mindlevel.api.ControllerCallback;
import net.mindlevel.api.UserController;
import net.mindlevel.model.User;
import net.mindlevel.util.ImageUtil;

import java.io.File;

import static net.mindlevel.util.ImageUtil.PICK_IMAGE;
import static net.mindlevel.util.ImageUtil.REQUEST_IMAGE_CAPTURE;

/**
 * A login screen that offers login via username/password.
 */
public class EditUserActivity extends AppCompatActivity {

    private UserController userController;
    private ImageUtil utils;
    private Uri path = null;

    // UI references.
    private ImageView imageView;
    private EditText passwordView1;
    private EditText passwordView2;
    private EditText descriptionView;
    private ProgressBar progressBar;
    private View editFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        userController = new UserController(getApplicationContext());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_edit_profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        utils = new ImageUtil(this);
        FloatingActionButton choosePicture = (FloatingActionButton) findViewById(R.id.choose_picture);
        choosePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utils.dispathGalleryIntent();
            }
        });

        FloatingActionButton takePicture = (FloatingActionButton) findViewById(R.id.take_picture);
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

        imageView = (ImageView)findViewById(R.id.image);

        passwordView1 = (EditText) findViewById(R.id.password1);
        passwordView2 = (EditText) findViewById(R.id.password2);
        passwordView1.requestFocus();

        descriptionView = (EditText) findViewById(R.id.description);

        User user = (User) getIntent().getSerializableExtra("user");


        Button applyButton = (Button) findViewById(R.id.apply_button);
        applyButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptEdit();
            }
        });

        editFormView = findViewById(R.id.edit_form);
        progressBar = (ProgressBar) findViewById(R.id.edit_progress);

        descriptionView.setText(user.description);
        if(user.image != null && !user.image.isEmpty()) {
            String url = ImageUtil.getUrl(user.image);
            Glide.with(this)
                    .load(url)
                    .listener(new ProgressController(progressBar))
                    .into(imageView);

        }
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

        boolean cancel = false;
        View focusView = null;

        if (!password1.equals(password2)) {
            passwordView1.setError(getString(R.string.error_invalid_password2));
            focusView = passwordView2;
            cancel = true;
        }

        if (!isPasswordValid(password1)) {
            passwordView1.setError(getString(R.string.error_invalid_password));
            focusView = passwordView1;
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
        return password.length() > 4 || password.isEmpty();
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

        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        progressBar.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                setResult(RESULT_OK);
                finish();
            } else {
                passwordView1.setError(getString(R.string.error_incorrect_password));
                passwordView1.requestFocus();
            }
        }
    };
}

