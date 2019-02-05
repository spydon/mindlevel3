package net.mindlevel.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import net.mindlevel.R;
import net.mindlevel.api.CommentController;
import net.mindlevel.api.ControllerCallback;
import net.mindlevel.impl.comment.CommentRecyclerViewAdapter;
import net.mindlevel.model.Comment;
import net.mindlevel.util.PreferencesUtil;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ChatActivity extends AppCompatActivity {
    private final static int CHAT_THREAD_ID = 0;
    private final static int UPDATE_INTERVAL = 3000;
    private Activity activity;
    private Handler handler = new Handler();
    private CommentRecyclerViewAdapter commentAdapter;
    private CommentController commentController;
    private EditText commentBox;
    private RecyclerView commentRecyclerView;
    private View commentProgress;
    private List<Comment> comments;
    private Comment comment;
    private long lastTimestamp = 0;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        this.activity = this;
        this.commentBox = findViewById(R.id.comment_box);
        this.commentRecyclerView = findViewById(R.id.comments);
        this.comments = new ArrayList<>();
        this.commentAdapter = new CommentRecyclerViewAdapter(activity, comments);
        this.commentProgress = findViewById(R.id.comment_progress);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ImageButton postButton = findViewById(R.id.post_button);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comment = getComment();
                if (!comment.comment.isEmpty()) {
                    commentProgress.setVisibility(VISIBLE);
                    commentController.add(comment, addCommentCallback);
                }
            }
        });

        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        commentRecyclerView.setAdapter(commentAdapter);

        this.commentController = new CommentController(this);
    }

    private final Runnable commentUpdate = new Runnable() {
        public void run() {
            refreshComments();
            handler.postDelayed(this, UPDATE_INTERVAL);
        }
    };

    private void refreshComments() {
        commentController.getThreadSince(CHAT_THREAD_ID, lastTimestamp, commentsCallback);
    }

    @Override
    protected void onStart() {
        super.onStart();
        handler.post(commentUpdate);
     }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacksAndMessages(null);
    }

    private Comment getComment() {
        String username = PreferencesUtil.getUsername(getApplicationContext());
        return new Comment(CHAT_THREAD_ID, commentBox.getText().toString(), username);
    }

    private ControllerCallback<List<Comment>> commentsCallback = new ControllerCallback<List<Comment>>() {
        @Override
        public void onPostExecute(Boolean isSuccess, List<Comment> response) {
            if (isSuccess) {
               commentProgress.setVisibility(GONE);
                if (!comments.containsAll(response)) {
                    comments.addAll(response);
                    commentAdapter.notifyDataSetChanged();
                    lastTimestamp = response.get(response.size()-1).created;
                }
            } else {
                Log.e("mindlevel", "Failed to get chat comments");
            }
        }
    };

    private ControllerCallback<Void> addCommentCallback = new ControllerCallback<Void>() {
        @Override
        public void onPostExecute(Boolean isSuccess, Void response) {
            commentProgress.setVisibility(GONE);
            if (isSuccess) {
                commentBox.setText("");
                commentRecyclerView.setVisibility(VISIBLE);
                refreshComments();
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
