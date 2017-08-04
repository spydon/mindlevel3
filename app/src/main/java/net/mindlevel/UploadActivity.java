package net.mindlevel;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UploadActivity extends AppCompatActivity {

    private AccomplishmentController controller;
    private TextView titleView, descriptionView;
    private Button uploadButton;
    private int missionId = -1;
    private Uri path = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        controller = new AccomplishmentController(getApplicationContext());

        final Mission mission = (Mission) getIntent().getSerializableExtra("mission");
        missionId = mission.id;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(mission.title);
        setSupportActionBar(toolbar);

        FloatingActionButton choosePicture = (FloatingActionButton) findViewById(R.id.choose_picture);
        choosePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispathGalleryIntent();
            }
        });

        FloatingActionButton takePicture = (FloatingActionButton) findViewById(R.id.take_picture);
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
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

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int PICK_IMAGE = 2;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                // TODO: Handle.
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "net.mindlevel",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private String currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                timestamp,      /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispathGalleryIntent() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                path = Uri.fromFile(new File(currentPhotoPath));
            } else if (requestCode == PICK_IMAGE) {
                if (data == null) {
                    // TODO: Display an error
                    return;
                }
                path = data.getData();
            }
            try {
                double maxLength = 2048;
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), path);
                ImageView imageView = (ImageView) findViewById(R.id.image);
                int height = bitmap.getHeight();
                int width = bitmap.getWidth();
                if (width > maxLength && width > height) {
                    int newHeight = (int)(height * (maxLength / width));
                    bitmap = Bitmap.createScaledBitmap(bitmap, (int)maxLength, newHeight, true);
                } else if (height > maxLength && height > width) {
                    int newWidth = (int)(width * (maxLength / height));
                    bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, (int)maxLength, true);
                }
                imageView.setImageBitmap(bitmap);
                uploadButton.setActivated(true);
            } catch (IOException ioe) {
                // TODO: Handle.
            }
        }
    }

    private ControllerCallback<Void> uploadCallback = new ControllerCallback<Void>() {

        @Override
        public void onPostExecute(final Boolean success, final Void nothing) {
            if (success) {
                Context context = getApplicationContext();
                Toast.makeText(context, R.string.action_upload, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                // TODO: Handle error
            }
        }
    };
}
