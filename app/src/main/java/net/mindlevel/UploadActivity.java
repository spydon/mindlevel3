package net.mindlevel;

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

import net.mindlevel.api.AccomplishmentController;
import net.mindlevel.api.ControllerCallback;
import net.mindlevel.model.Accomplishment;
import net.mindlevel.model.Mission;

import java.io.File;

import static net.mindlevel.ImageUtil.PICK_IMAGE;
import static net.mindlevel.ImageUtil.REQUEST_IMAGE_CAPTURE;

public class UploadActivity extends AppCompatActivity {

    private AccomplishmentController controller;
    private TextView titleView, descriptionView;
    private Button uploadButton;
    private int missionId = -1;
    private Uri path = null;
    private ImageUtil utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        controller = new AccomplishmentController(getApplicationContext());
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

        titleView = (TextView) findViewById(R.id.title);
        descriptionView = (TextView) findViewById(R.id.description);
        titleView.setText(mission.title);
        uploadButton = (Button) findViewById(R.id.upload_button);
        uploadButton.setActivated(false);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadButton.setActivated(false);
                Accomplishment accomplishment = new Accomplishment(0, titleView.getText().toString(),
                        descriptionView.getText().toString(), "", missionId, 0, 0);
                controller.add(accomplishment, path, uploadCallback);
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

    private ControllerCallback<Void> uploadCallback = new ControllerCallback<Void>() {

        @Override
        public void onPostExecute(final Boolean success, final Void nothing) {
            if (success) {
                Context context = getApplicationContext();
                Toast.makeText(context, R.string.successful_upload, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                // TODO: Handle error
            }
        }
    };
}
