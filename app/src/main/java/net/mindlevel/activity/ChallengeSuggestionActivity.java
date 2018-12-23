package net.mindlevel.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.yalantis.ucrop.UCrop;

import net.mindlevel.R;
import net.mindlevel.api.ChallengeController;
import net.mindlevel.api.ControllerCallback;
import net.mindlevel.model.Challenge;
import net.mindlevel.util.ImageUtil;
import net.mindlevel.util.KeyboardUtil;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static net.mindlevel.util.ImageUtil.PICK_IMAGE;

public class ChallengeSuggestionActivity extends AppCompatActivity {

    private ChallengeController challengeController;
    private ScrollView containerView;
    private View progressView;
    private TextView titleView, descriptionView, errorView;
    private Button uploadButton;
    private Uri path = null;
    private ImageUtil utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_suggestion);
        Context context = getApplicationContext();
        challengeController = new ChallengeController(context);
        utils = new ImageUtil(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_challenge_suggestion);
        setSupportActionBar(toolbar);

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device does not have a camera
            takePicture.setVisibility(View.INVISIBLE);
        }

        containerView = findViewById(R.id.scroll);
        progressView = findViewById(R.id.progress);
        showProgress(false);

        errorView = findViewById(R.id.error_text);
        titleView = findViewById(R.id.title);
        descriptionView = findViewById(R.id.description);
        uploadButton = findViewById(R.id.upload_button);
        uploadButton.setActivated(false);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String description = descriptionView.getText().toString();
                if (path == null) {
                    setError(true, getString(R.string.error_no_image));
                } else if (description.isEmpty()) {
                    setError(true, getString(R.string.error_no_description));
                } else {
                    setError(false);
                    showProgress(true);
                    uploadButton.setActivated(false);
                    String title = titleView.getText().toString();
                    Challenge challenge = new Challenge(title, description);
                    challengeController.add(challenge, path, uploadCallback);
                }
            }
        });
    }

    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        if (show) {
            KeyboardUtil.hideKeyboard(this);
        }

        containerView.setVisibility(show ? GONE : VISIBLE);
        containerView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                containerView.setVisibility(show ? GONE : VISIBLE);
            }
        });

        progressView.setVisibility(show ? VISIBLE : GONE);
        progressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressView.setVisibility(show ? VISIBLE : GONE);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ImageView imageView = findViewById(R.id.image);
        Uri maybePath = utils.handleImageResult(requestCode, resultCode, true, data, imageView, this);
        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK && maybePath != null) {
            path = maybePath;
        } else if (requestCode == PICK_IMAGE && data == null) {
            setError(true, getString(R.string.error_image_loading));
        }
    }

    private void setError(boolean isError) {
        setError(isError, "");
    }

    private void setError(boolean isError, String error) {
        containerView.smoothScrollTo(0,0);
        errorView.setVisibility(isError ? VISIBLE : GONE);
        errorView.setText(error);
    }

    private ControllerCallback<Void> uploadCallback = new ControllerCallback<Void>() {

        @Override
        public void onPostExecute(final Boolean success, final Void response) {
            showProgress(false);
            if (success) {
                Context context = getApplicationContext();
                Toast.makeText(context, R.string.successful_suggestion, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                setError(true, getString(R.string.error_upload_timeout));
            }
        }
    };
}
