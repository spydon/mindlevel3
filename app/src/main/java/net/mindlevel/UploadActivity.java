package net.mindlevel;

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
import android.widget.TextView;
import android.widget.Toast;

import com.pchmn.materialchips.ChipsInput;
import com.pchmn.materialchips.model.ChipInterface;

import net.mindlevel.api.AccomplishmentController;
import net.mindlevel.api.ControllerCallback;
import net.mindlevel.api.UserController;
import net.mindlevel.model.Accomplishment;
import net.mindlevel.model.Mission;
import net.mindlevel.model.User;
import net.mindlevel.util.ImageUtil;
import net.mindlevel.util.KeyboardUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static net.mindlevel.util.ImageUtil.PICK_IMAGE;
import static net.mindlevel.util.ImageUtil.REQUEST_IMAGE_CAPTURE;

public class UploadActivity extends AppCompatActivity {

    private AccomplishmentController accomplishmentController;
    private UserController userController;
    private View containerView, progressView;
    private TextView titleView, descriptionView;
    private ChipsInput contributorInput;
    private Button uploadButton;
    private Context context;
    private int missionId = -1;
    private Uri path = null;
    private ImageUtil utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        context = getApplicationContext();
        accomplishmentController = new AccomplishmentController(context);
        userController = new UserController(context);
        utils = new ImageUtil(this);

        final Mission mission = (Mission) getIntent().getSerializableExtra("mission");
        missionId = mission.id;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(mission.title);
        setSupportActionBar(toolbar);

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device does not have a camera
            takePicture.setVisibility(View.INVISIBLE);
        }

        containerView = findViewById(R.id.scroll);
        progressView = findViewById(R.id.progress);
        showProgress(false);

        titleView = (TextView) findViewById(R.id.title);
        descriptionView = (TextView) findViewById(R.id.description);
        titleView.setText(mission.title);
        uploadButton = (Button) findViewById(R.id.upload_button);
        uploadButton.setActivated(false);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgress(true);
                uploadButton.setActivated(false);
                List<String> contributors = new ArrayList<>();
                for(ChipInterface chip : contributorInput.getSelectedChipList()) {
                    contributors.add(chip.getLabel());
                }
                contributors.add("spydon");
                Accomplishment accomplishment = new Accomplishment(0, titleView.getText().toString(),
                        descriptionView.getText().toString(), "", missionId, 0, 0);
                accomplishmentController.add(accomplishment, contributors, path, uploadCallback);
            }
        });

        contributorInput = (ChipsInput) findViewById(R.id.contributor_input);
        userController.getAll(usernamesCallback);
    }

    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        if(show) {
            KeyboardUtil.hideKeyboard(this);
        }

        containerView.setVisibility(show ? View.GONE : View.VISIBLE);
        containerView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                containerView.setVisibility(show ? View.GONE : View.VISIBLE);
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

    private ControllerCallback<Accomplishment> uploadCallback = new ControllerCallback<Accomplishment>() {

        @Override
        public void onPostExecute(final Boolean success, final Accomplishment accomplishment) {
            showProgress(false);
            if (success) {
                Context context = getApplicationContext();
                Toast.makeText(context, R.string.successful_upload, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                // TODO: Handle error
                System.out.println("Failed with upload...");
            }
        }
    };

    private ControllerCallback<List<User>> usernamesCallback = new ControllerCallback<List<User>>() {

        @Override
        public void onPostExecute(final Boolean success, final List<User> users) {
            if (success) {
                ArrayList<UserChip> userChips = new ArrayList<>();
                for(User user : users) {
                    userChips.add(new UserChip(user));
                }
                contributorInput.setFilterableList(userChips);
            }
        }
    };
}
