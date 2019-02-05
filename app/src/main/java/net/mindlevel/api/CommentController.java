package net.mindlevel.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import net.mindlevel.api.endpoint.CommentEndpoint;
import net.mindlevel.model.Comment;
import net.mindlevel.model.Count;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentController extends BackendService {

    private static CommentEndpoint endpoint;

    public CommentController(Context context) {
        super(context);
        endpoint = retrofit.create(CommentEndpoint.class);
    }

    public void getThread(final int threadId, final ControllerCallback<List<Comment>> callback) {
        Call<List<Comment>> call = endpoint.getThread(threadId);
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(@NonNull Call<List<Comment>> call, @NonNull Response<List<Comment>> response) {
                if (response.isSuccessful()) {
                    callback.onPostExecute(true, response.body());
                } else {
                    onFailure(call, new Throwable("Could not fetch comments remotely"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Comment>> call, @NonNull Throwable t) {
                callback.onPostExecute(false, null);
                t.printStackTrace();
                Log.w("mindlevel", "getThread comments call failed");
            }
        });
    }

    public void getThreadSince(final int threadId,
                               final long timestamp,
                               final ControllerCallback<List<Comment>> callback) {
        Call<List<Comment>> call = endpoint.getThreadSince(threadId, timestamp);
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(@NonNull Call<List<Comment>> call, @NonNull Response<List<Comment>> response) {
                if (response.isSuccessful()) {
                    callback.onPostExecute(true, response.body());
                } else {
                    onFailure(call, new Throwable("Could not fetch comments remotely"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Comment>> call, @NonNull Throwable t) {
                callback.onPostExecute(false, null);
                t.printStackTrace();
                Log.w("mindlevel", "getThread since call failed");
            }
        });
    }

    public void get(final int id, final ControllerCallback<Comment> callback) {
        Call<Comment> call = endpoint.get(id);
        call.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(@NonNull Call<Comment> call, @NonNull Response<Comment> response) {
                if (response.isSuccessful()) {
                    callback.onPostExecute(true, response.body());
                } else {
                    callback.onPostExecute(false, null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Comment> call, @NonNull Throwable t) {
                callback.onPostExecute(false, null);
                t.printStackTrace();
                Log.w("mindlevel", "get comment call failed");
            }
        });
    }

    public void getCount(final int threadId, final ControllerCallback<Count> callback) {
        Call<Count> call = endpoint.getCount(threadId);
        call.enqueue(new Callback<Count>() {
            @Override
            public void onResponse(@NonNull Call<Count> call, @NonNull Response<Count> response) {
                if (response.isSuccessful()) {
                    callback.onPostExecute(true, response.body());
                } else {
                    callback.onPostExecute(false, null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Count> call, @NonNull Throwable t) {
                callback.onPostExecute(false, null);
                t.printStackTrace();
                Log.w("mindlevel", "get comment count call failed");
            }
        });
    }

    public void add(final Comment comment, final ControllerCallback<Void> callback) {
        Call<Void> call = endpoint.add(comment);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onPostExecute(true, response.body());
                } else {
                    callback.onPostExecute(false, null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                callback.onPostExecute(false, null);
                t.printStackTrace();
                Log.w("mindlevel", "post comment call failed");
            }
        });
    }
}
