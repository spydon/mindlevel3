package net.mindlevel.api;

import android.content.Context;
import android.net.Uri;

import net.mindlevel.api.endpoint.AccomplishmentEndpoint;
import net.mindlevel.model.Accomplishment;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccomplishmentController extends BackendService {

    private static AccomplishmentEndpoint endpoint;

    public AccomplishmentController(Context context) {
        super(context);
        endpoint = retrofit.create(AccomplishmentEndpoint.class);
    }

    public void add(final Accomplishment accomplishment, final Uri uri, final ControllerCallback<Void> callback) {
        InputStream is = null;
        try {
            is = context.getContentResolver().openInputStream(uri);
            byte[] bytes = IOUtils.toByteArray(is);

            MultipartBody.Part image = MultipartBody.Part.createFormData("image", null, RequestBody.create
                    (MediaType.parse("image/*"), bytes));
            Call<Void> call = endpoint.add(accomplishment, image);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if(response.isSuccessful()) {
                        callback.onPostExecute(true, null);
                    } else {
                        callback.onPostExecute(false, null);
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    callback.onPostExecute(false, null);
                    t.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(is != null) {
                try {
                    is.close();
                }
                catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
