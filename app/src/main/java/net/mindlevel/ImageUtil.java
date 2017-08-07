package net.mindlevel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
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

import static android.app.Activity.RESULT_OK;

public class ImageUtil {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int PICK_IMAGE = 2;

    private final Activity activity;
    private static String bucketAddress = "";

    public ImageUtil(Activity activity) {
        this.activity = activity;
    }

    public static String getUrl(String filename) {
        return bucketAddress + filename;
    }

    public static void setBucketAddress(String bucketAddress) {
        ImageUtil.bucketAddress = bucketAddress;
    }

    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
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
                Uri photoURI = FileProvider.getUriForFile(activity, "net.mindlevel", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private String currentPhotoPath;

    public String getPhotoPath() {
        return currentPhotoPath;
    }

    public File createImageFile() throws IOException {
        // Create an image file name
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                timestamp,      /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void dispathGalleryIntent() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        activity.startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    protected void setImage(Uri path, ImageView view) {
        try {
            double maxLength = 2048;
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), path);
            int height = bitmap.getHeight();
            int width = bitmap.getWidth();
            if (width > maxLength && width > height) {
                int newHeight = (int)(height * (maxLength / width));
                bitmap = Bitmap.createScaledBitmap(bitmap, (int)maxLength, newHeight, true);
            } else if (height > maxLength && height > width) {
                int newWidth = (int)(width * (maxLength / height));
                bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, (int)maxLength, true);
            }
            view.setImageBitmap(bitmap);
        } catch (IOException ioe) {
            // TODO: Handle.
        }
    }
}
