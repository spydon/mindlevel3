package net.mindlevel.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pchmn.materialchips.ChipView;

import net.mindlevel.R;
import net.mindlevel.api.ChallengeController;
import net.mindlevel.api.ControllerCallback;
import net.mindlevel.impl.ProgressController;
import net.mindlevel.model.Challenge;
import net.mindlevel.model.Level;
import net.mindlevel.util.CoordinatorUtil;
import net.mindlevel.util.ImageUtil;
import net.mindlevel.util.NetworkUtil;

public class ChallengeActivity extends AppCompatActivity {

    private ChallengeController controller;
    private View challengeView, progressView;
    private ProgressBar imageProgressView;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);
        challengeView = findViewById(R.id.challenge_content);
        progressView = findViewById(R.id.progress);
        imageProgressView = findViewById(R.id.image_progress);
        controller = new ChallengeController(challengeView.getContext());
        context = this;

        showProgress(true);
        if (getIntent().hasExtra("challenge")) {
            Challenge challenge = (Challenge) getIntent().getSerializableExtra("challenge");
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle(challenge.title);
            challengeCallback.onPostExecute(true, challenge);
        } else if (getIntent().hasExtra("challenge_id")) {
            int ChallengeId = getIntent().getIntExtra("challenge_id", -1);
            controller.get(ChallengeId, challengeCallback);
        }
    }

    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        challengeView.setVisibility(show ? View.GONE : View.VISIBLE);
        challengeView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                challengeView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        progressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    private ControllerCallback<Challenge> challengeCallback = new ControllerCallback<Challenge>() {

        @Override
        public void onPostExecute(final Boolean success, final Challenge challenge) {
            showProgress(false);

            if (success) {
                Toolbar toolbar = findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                toolbar.setTitle(challenge.title);

                TextView titleView = findViewById(R.id.title);
                titleView.setText(challenge.title);

                TextView levelView = findViewById(R.id.level);
                levelView.setText(new Level(challenge.levelRestriction).getVisualLevel());

                TextView descriptionView = findViewById(R.id.description);
                descriptionView.setText(challenge.description);

                ChipView creatorView = findViewById(R.id.creator);
                creatorView.setLabel(challenge.creator);

                creatorView.setOnChipClicked(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CoordinatorUtil.toUser(context, challenge.creator);
                    }
                });

                FloatingActionButton uploadButton = findViewById(R.id.upload_button);
                uploadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent uploadIntent = new Intent(context, UploadActivity.class);
                        uploadIntent.putExtra("challenge", challenge);
                        startActivity(uploadIntent);
                    }
                });

                FloatingActionButton accomplishmentsButton =
                        findViewById(R.id.accomplishments_button);
                accomplishmentsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CoordinatorUtil.toFeed(context, challenge);
                    }
                });

                ImageView imageView = findViewById(R.id.image);
                String url = ImageUtil.getUrl(challenge.image);
                Glide.with(context)
                        .load(url)
                        .listener(new ProgressController(imageProgressView))
                        .into(imageView);

                if (!NetworkUtil.connectionCheck(context, challengeView)) {
                    uploadButton.setVisibility(View.GONE);
                    accomplishmentsButton.setVisibility(View.GONE);
                }
            } else {
                // TODO: Show error
            }
        }
    };

}
