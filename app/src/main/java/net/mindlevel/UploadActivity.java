package net.mindlevel;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.mindlevel.model.Mission;

public class UploadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        final Mission mission = (Mission) getIntent().getSerializableExtra("mission");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(mission.title);
        setSupportActionBar(toolbar);

        FloatingActionButton choosePicture = (FloatingActionButton) findViewById(R.id.choose_picture);
        choosePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispathGalleryIntent();
                Snackbar.make(view, mission.title, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        FloatingActionButton takePicture = (FloatingActionButton) findViewById(R.id.take_picture);
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
                Snackbar.make(view, mission.description, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device does not have a camera
            takePicture.setVisibility(View.INVISIBLE);
        }

        //ImageView imageView = (ImageView) findViewById(R.id.mission_image);
        //Glide.with(this).load(mission.imageUrl).into(imageView);

        TextView titleView = (TextView) findViewById(R.id.title);
        //TextView descriptionView = (TextView) findViewById(R.id.mission_description);
        titleView.setText(mission.title);
        //descriptionView.setText(mission.description);
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int PICK_IMAGE = 2;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
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
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ImageView imageView = (ImageView) findViewById(R.id.image);
            imageView.setImageBitmap(imageBitmap);
        } else if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            // Handle inputstream
            // InputStream inputStream = getContentResolver().openInputStream(data.getData());
        }
    }
}
