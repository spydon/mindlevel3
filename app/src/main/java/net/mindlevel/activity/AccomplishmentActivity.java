package net.mindlevel.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.pchmn.materialchips.ChipView;

import net.mindlevel.R;
import net.mindlevel.api.AccomplishmentController;
import net.mindlevel.api.ChallengeController;
import net.mindlevel.api.CommentController;
import net.mindlevel.api.ControllerCallback;
import net.mindlevel.impl.recycler.ContributorRecyclerViewAdapter;
import net.mindlevel.impl.Glassbar;
import net.mindlevel.impl.ImageLikeView;
import net.mindlevel.impl.ProgressController;
import net.mindlevel.impl.recycler.CommentRecyclerViewAdapter;
import net.mindlevel.model.Accomplishment;
import net.mindlevel.model.Challenge;
import net.mindlevel.model.Comment;
import net.mindlevel.model.Level;
import net.mindlevel.model.User;
import net.mindlevel.util.ImageUtil;
import net.mindlevel.util.PermissionUtil;
import net.mindlevel.util.PreferencesUtil;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class AccomplishmentActivity extends InfoActivity {

    private static final int SHARE_PERMISSION = 101;
    private View coordinator;
    private ImageLikeView imageView;
    private Activity activity;
    private Handler handler = new Handler();
    private ContributorRecyclerViewAdapter contributorAdapter;
    private CommentRecyclerViewAdapter commentAdapter;
    private CommentController commentController;
    private EditText commentBox;
    private RecyclerView commentRecyclerView;
    private View contributorProgress, commentProgress, challengeProgress;
    private ChipView challengeChip;
    private TextView levelView;
    private List<User> contributors;
    private List<Comment> comments;
    private Comment comment;
    private Challenge challenge;
    private Accomplishment accomplishment;
    private long lastTimestamp = 0;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accomplishment);
        final Context context = getBaseContext();
        this.activity = this;
        this.accomplishment = null;

        // For the info center
        initializeViews();

        this.coordinator = findViewById(R.id.coordinator);
        this.commentBox = findViewById(R.id.comment_box);
        this.commentRecyclerView = findViewById(R.id.comments);
        this.contributorProgress = findViewById(R.id.contributor_progress);
        this.commentProgress = findViewById(R.id.comment_progress);
        this.challengeProgress = findViewById(R.id.challenge_progress);
        this.challengeChip = findViewById(R.id.challenge_chip);
        this.levelView = findViewById(R.id.level);

        this.contributors = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.contributorAdapter = new ContributorRecyclerViewAdapter(activity, contributors);
        this.commentAdapter = new CommentRecyclerViewAdapter(activity, comments);
        AccomplishmentController accomplishmentController = new AccomplishmentController(context);
        this.commentController = new CommentController(this);

        showInfo(false, true);
        if (getIntent().hasExtra("accomplishment")) {
            Accomplishment accomplishment = (Accomplishment) getIntent().getSerializableExtra("accomplishment");
            accomplishmentCallback.onPostExecute(true, accomplishment);
        } else if (getIntent().hasExtra("accomplishment_id")) {
            int accomplishmentId = getIntent().getIntExtra("accomplishment_id", -1);
            accomplishmentController.get(accomplishmentId, accomplishmentCallback);
        }
    }

    private void setAccomplishment(Accomplishment accomplishment) {
        this.accomplishment = accomplishment;
    }

    private final Runnable commentUpdate = new Runnable() {
        public void run() {
            if (accomplishment != null) {
                refreshComments(accomplishment.id);
            }
            handler.postDelayed(this, 10000);
        }
    };

    private void refreshComments(int accomplishmentId) {
        commentController.getThreadSince(accomplishmentId, lastTimestamp, commentsCallback);
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
        return new Comment(accomplishment.id, commentBox.getText().toString(), username);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
            String[] permissions, int[] grantResults) {
        switch (requestCode) {
        case SHARE_PERMISSION:
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                final Accomplishment accomplishment = (Accomplishment) getIntent().getSerializableExtra("accomplishment");
                sharePicture(accomplishment);
            } else {
                Glassbar.make(coordinator, getString(R.string.permission_sharing_denied), Snackbar.LENGTH_LONG).show();
            }
            break;
        default:
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private ControllerCallback<List<User>> contributorsCallback = new ControllerCallback<List<User>>() {
        @Override
        public void onPostExecute(Boolean isSuccess, List<User> response) {
            contributorProgress.setVisibility(GONE);
            if (isSuccess) {
                contributors.clear();
                contributors.addAll(response);
                contributorAdapter.notifyDataSetChanged();
            }
        }
    };

    private ControllerCallback<Challenge> challengeCallback = new ControllerCallback<Challenge>() {
        @Override
        public void onPostExecute(Boolean isSuccess, final Challenge response) {
            if (isSuccess) {
                challenge = response;
                challengeChip.setLabel(challenge.title);
                Level level = new Level(challenge.levelRestriction);
                String levelText = getString(R.string.title_level, level.getVisualLevel());
                levelView.setText(levelText);
                challengeChip.setOnChipClicked(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent challengeIntent = new Intent(activity, ChallengeActivity.class);
                        challengeIntent.putExtra("challenge", challenge);
                        startActivity(challengeIntent);
                    }
                });
                challengeProgress.setVisibility(GONE);
                challengeChip.setVisibility(VISIBLE);
            } else {
                challengeProgress.setVisibility(GONE);
                challengeChip.setVisibility(GONE);
                levelView.setVisibility(GONE);
                findViewById(R.id.challenge_title).setVisibility(GONE);
                Log.e("mindlevel", "Failed to get challenge");
            }
        }
    };

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
                Log.e("mindlevel", "Failed to get comments");
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
                refreshComments(accomplishment.id);
            }
        }
    };

    private ControllerCallback<Accomplishment> accomplishmentCallback = new ControllerCallback<Accomplishment>() {

        @Override
        public void onPostExecute(final Boolean success, final Accomplishment accomplishment) {

            if (success || accomplishment != null) {
                showInfo(false, false);
                setAccomplishment(accomplishment);
                Toolbar toolbar = findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle(accomplishment.title);

                final String url = ImageUtil.getUrl(accomplishment.image);

                FloatingActionButton challengeButton = findViewById(R.id.fab_challenge);
                challengeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent challengeIntent = new Intent(activity, ChallengeActivity.class);
                        if (challenge != null) {
                            challengeIntent.putExtra("challenge", challenge);
                        } else {
                            challengeIntent.putExtra("challenge_id", accomplishment.challengeId);
                        }
                        startActivity(challengeIntent);
                    }
                });

                final FloatingActionButton shareButton = findViewById(R.id.fab_share);
                shareButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(PermissionUtil.executeWithPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                SHARE_PERMISSION)) {
                            sharePicture(accomplishment);
                        }
                    }
                });

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


                TextView imageText = findViewById(R.id.image_text);
                ProgressBar likeProgress = findViewById(R.id.progress_like);
                imageView = findViewById(R.id.image);
                imageView.setTextView(imageText);
                imageView.setProgressLike(likeProgress);
                imageView.setId(accomplishment.id);

                ProgressBar progressBar = findViewById(R.id.image_progress);
                Glide.with(activity)
                        .load(url)
                        .listener(new ProgressController(progressBar))
                        .into(imageView);

                Glide.with(activity)
                        .load(url)
                        .apply(bitmapTransform(new BlurTransformation(25, 3)))
                        .into(new SimpleTarget<Drawable>() {
                            @Override
                            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                                resource.setAlpha(160);
                                contentView.setBackground(resource);
                            }
                        });

                TextView titleView = findViewById(R.id.title);
                TextView scoreView = findViewById(R.id.score);
                TextView descriptionView = findViewById(R.id.description);
                titleView.setText(accomplishment.title);
                String scoreText = activity.getString(R.string.title_score, String.valueOf(accomplishment.score));
                scoreView.setText(scoreText);
                descriptionView.setText(accomplishment.description);

                imageView.setDrawingCacheEnabled(true);
                imageView.buildDrawingCache();

                AccomplishmentController controller = new AccomplishmentController(activity);
                controller.getContributors(accomplishment.id, contributorsCallback);

                RecyclerView contributorRecyclerView = findViewById(R.id.contributors);
                contributorRecyclerView.setLayoutManager(
                        new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
                contributorRecyclerView.setAdapter(contributorAdapter);

                commentRecyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
                commentRecyclerView.setAdapter(commentAdapter);


                ChallengeController challengeController = new ChallengeController(activity);
                challengeController.get(accomplishment.challengeId, challengeCallback);
            } else {
                showInfo(true, false);
            }
        }
    };

    private void sharePicture(Accomplishment accomplishment) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("image/*");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, accomplishment.title);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, accomplishment.description);
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "", null);
        Uri uri = Uri.parse(path);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
        activity.startActivity(Intent.createChooser(sharingIntent, getString(R.string.action_share_using)));
    }
}
