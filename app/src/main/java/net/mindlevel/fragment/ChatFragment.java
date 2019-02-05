package net.mindlevel.fragment;

// TODO: Change back to non-support lib

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import net.mindlevel.R;
import net.mindlevel.api.CommentController;
import net.mindlevel.api.ControllerCallback;
import net.mindlevel.impl.comment.CommentRecyclerViewAdapter;
import net.mindlevel.model.Comment;
import net.mindlevel.util.NetworkUtil;
import net.mindlevel.util.PreferencesUtil;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ChatFragment extends InfoFragment {
    private final static int CHAT_THREAD_ID = 0;
    private final static int UPDATE_INTERVAL = 3000;
    private Context context;
    private View view;
    private Handler handler = new Handler();
    private CommentRecyclerViewAdapter commentAdapter;
    private CommentController commentController;
    private EditText commentBox;
    private RecyclerView commentRecyclerView;
    private View commentProgress;
    private List<Comment> comments;
    private Comment comment;
    private long lastTimestamp;

    public ChatFragment() {
        if (getArguments() == null) {
            setArguments(new Bundle());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_chat, container, false);
        this.contentView = view.findViewById(R.id.content);
        this.context = getContext();

        this.commentBox = view.findViewById(R.id.comment_box);
        this.commentRecyclerView = view.findViewById(R.id.comments);
        this.comments = new ArrayList<>();
        this.lastTimestamp = 0;
        this.commentAdapter = new CommentRecyclerViewAdapter(context, comments);
        this.commentProgress = view.findViewById(R.id.comment_progress);

        final ImageButton postButton = view.findViewById(R.id.post_button);
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

        commentRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        commentRecyclerView.setAdapter(commentAdapter);

        this.commentController = new CommentController(context);

        if (!NetworkUtil.isConnected(context)) {
            showInfo(true, false);
        }

        return view;
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
    public void onResume() {
        super.onResume();
        handler.post(commentUpdate);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }

    private Comment getComment() {
        String username = PreferencesUtil.getUsername(context);
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
}
