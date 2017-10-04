package net.mindlevel.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageUtil {

    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int PICK_IMAGE = 2;

    private static final int MAX_IMAGE_WIDTH = 1024;
    private static final int MAX_IMAGE_HEIGHT = 2048;

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

    public void setImage(String image, boolean completePath, ImageView view) {
        Uri uri;
        if(completePath) {
            uri = Uri.parse(image);
        } else {
            uri = Uri.parse(getUrl(image));
        }
        setImage(uri, view);
    }

    public void setImage(Uri path, ImageView view) {
        try {
            Context context = view.getContext();
            double maxLength = 2048;
            //Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), path);
            Bitmap bitmap = getCorrectlyOrientedImage(context, path);
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

    private static int getOrientation(Context context, Uri photoUri) {
        /* it's on the external media. */
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);

        if (cursor == null || cursor.getCount() != 1) {
            return -1;
        }

        cursor.moveToFirst();
        int rotation = cursor.getInt(0);
        cursor.close();
        return rotation;
    }

    private static Bitmap getCorrectlyOrientedImage(Context context, Uri photoUri) throws IOException {
        InputStream is = context.getContentResolver().openInputStream(photoUri);
        BitmapFactory.Options dbo = new BitmapFactory.Options();
        dbo.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, dbo);
        is.close();

        int rotatedWidth, rotatedHeight;
        int orientation = getOrientation(context, photoUri);

        if (orientation == 90 || orientation == 270) {
            rotatedWidth = dbo.outHeight;
            rotatedHeight = dbo.outWidth;
        } else {
            rotatedWidth = dbo.outWidth;
            rotatedHeight = dbo.outHeight;
        }

        Bitmap srcBitmap;
        is = context.getContentResolver().openInputStream(photoUri);
        if (rotatedWidth > MAX_IMAGE_WIDTH || rotatedHeight > MAX_IMAGE_HEIGHT) {
            float widthRatio = ((float) rotatedWidth) / ((float) MAX_IMAGE_WIDTH);
            float heightRatio = ((float) rotatedHeight) / ((float) MAX_IMAGE_HEIGHT);
            float maxRatio = Math.max(widthRatio, heightRatio);

            // Create the bitmap from file
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = (int) maxRatio;
            srcBitmap = BitmapFactory.decodeStream(is, null, options);
        } else {
            srcBitmap = BitmapFactory.decodeStream(is);
        }
        is.close();

        /*
         * if the orientation is not 0 (or -1, which means we don't know), we
         * have to do a rotation.
         */
        if (orientation > 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);

            srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(),
                    srcBitmap.getHeight(), matrix, true);
        }

        return srcBitmap;
    }
}
