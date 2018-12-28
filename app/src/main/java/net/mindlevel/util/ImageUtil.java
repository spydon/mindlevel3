package net.mindlevel.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.widget.ImageView;

import com.yalantis.ucrop.UCrop;

import net.mindlevel.R;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;

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
            File photoFile = createImageFile();

            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(activity, "net.mindlevel", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private String currentPhotoPath;

    public Uri getPhotoUri() {
        // TODO: Change to Optional<Uri> when on API 24
        if (currentPhotoPath != null && new File(currentPhotoPath).exists()) {
            return Uri.fromFile(new File(currentPhotoPath));
        } else {
            return null;
        }
    }

    private File createImageFile() {
        // Create an image file name
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(
                    timestamp,      /* prefix */
                    ".jpg",   /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException ex) {
            // Error occurred while creating the File
            // TODO: Handle.
        }


        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private Uri copyImageFromContent(Uri uri, ContentResolver contentResolver) {
        File file = createImageFile();
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri);
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (IOException ioe) {
            // TODO: Handle.
        }
        return Uri.fromFile(file);
    }

    public void dispatchGalleryIntent() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        activity.startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    public void setImage(String image, boolean completePath, ImageView view) {
        Uri uri;
        if (completePath) {
            uri = Uri.parse(image);
        } else {
            uri = Uri.parse(getUrl(image));
        }
        setImage(uri, view);
    }

    public void setImage(Uri path, ImageView view) {
        try {
            Context context = view.getContext();
            double maxWidth = 2048;
            //Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), path);
            Bitmap bitmap = getCorrectlyOrientedImage(context, path);
            int height = bitmap.getHeight();
            int width = bitmap.getWidth();
            if (width > maxWidth && width > height) {
                int newHeight = (int)(height * (maxWidth / width));
                bitmap = Bitmap.createScaledBitmap(bitmap, (int)maxWidth, newHeight, true);
            } else if (height > maxWidth && height > width) {
                int newWidth = (int)(width * (maxWidth / height));
                bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, (int)maxWidth, true);
            }
            view.setImageBitmap(bitmap);
        } catch (IOException ioe) {
            // TODO: Handle.
        }
    }

    public Uri handleImageResult(int requestCode, int resultCode, boolean isSquare, Intent data, ImageView imageView,
                                 Activity parent) {
        Uri path = getPhotoUri();
        if (resultCode == RESULT_OK) {
            if (requestCode != UCrop.REQUEST_CROP) {
                if (requestCode == REQUEST_IMAGE_CAPTURE) {
                    path = getPhotoUri();
                } else if (requestCode == PICK_IMAGE) {
                    if (data == null) {
                        // TODO: Display an error
                        return path;
                    }
                    path = copyImageFromContent(data.getData(), parent.getContentResolver());
                }

                UCrop.Options options = new UCrop.Options();
                options.setStatusBarColor(parent.getResources().getColor(R.color.colorBarDark));
                options.setToolbarColor(parent.getResources().getColor(R.color.colorPrimary));

                UCrop cropper = UCrop.of(path, path).withOptions(options);
                if (isSquare) {
                    cropper.withAspectRatio(1F, 1F);
                }
                cropper.start(parent);
            } else {
                setImage(path, imageView);
            }
        }
        return path;
    }

    public static Uri uriFromDrawable(int res) {
        return Uri.parse("android.resource://net.mindlevel/" + res);
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

    public static byte[] compressImage(Uri path, Context context) throws IOException {
        InputStream is = context.getContentResolver().openInputStream(path);

        File outputDir = context.getCacheDir();
        File targetFile = File.createTempFile("mindlevel", ".jpg", outputDir);

        byte[] bytes;
        if (is != null) {
            FileUtils.copyInputStreamToFile(is, targetFile);
            File compressed = new Compressor(context).compressToFile(targetFile);
            bytes = FileUtils.readFileToByteArray(compressed);
        } else {
            bytes = new byte[0];
        }

        return bytes;
    }
}
