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

import net.mindlevel.util.CoordinatorUtil;
import net.mindlevel.impl.ProgressController;
import net.mindlevel.R;
import net.mindlevel.api.ControllerCallback;
import net.mindlevel.api.ChallengeController;
import net.mindlevel.model.Challenge;
import net.mindlevel.util.ImageUtil;
import net.mindlevel.util.NetworkUtil;

public class ChallengeActivity extends AppCompatActivity {

    private ChallengeController controller;
    private View ChallengeView, progressView;
    private ProgressBar imageProgressView;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);
        ChallengeView = findViewById(R.id.challenge_content);
        progressView = findViewById(R.id.progress);
        imageProgressView = (ProgressBar) findViewById(R.id.image_progress);
        controller = new ChallengeController(ChallengeView.getContext());
        context = this;

        showProgress(true);
        if (getIntent().hasExtra("challenge")) {
            Challenge Challenge = (Challenge) getIntent().getSerializableExtra("challenge");
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle(Challenge.title);
            ChallengeCallback.onPostExecute(true, Challenge);
        } else if (getIntent().hasExtra("ChallengeId")) {
            int ChallengeId = getIntent().getIntExtra("ChallengeId", -1);
            controller.get(ChallengeId, ChallengeCallback);
        }
    }

    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        ChallengeView.setVisibility(show ? View.GONE : View.VISIBLE);
        ChallengeView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ChallengeView.setVisibility(show ? View.GONE : View.VISIBLE);
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

    private ControllerCallback<Challenge> ChallengeCallback = new ControllerCallback<Challenge>() {

        @Override
        public void onPostExecute(final Boolean success, final Challenge Challenge) {
            showProgress(false);

            if (success) {
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                toolbar.setTitle(Challenge.title);

                TextView titleView = (TextView) findViewById(R.id.title);
                titleView.setText(Challenge.title);

                TextView descriptionView = (TextView) findViewById(R.id.description);
                descriptionView.setText(Challenge.description);

                ChipView creatorView = (ChipView) findViewById(R.id.creator);
                creatorView.setLabel(Challenge.creator);

                creatorView.setOnChipClicked(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CoordinatorUtil.toUser(context, Challenge.creator);
                    }
                });

                FloatingActionButton uploadButton = (FloatingActionButton) findViewById(R.id.upload_button);
                uploadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent uploadIntent = new Intent(context, UploadActivity.class);
                        uploadIntent.putExtra("challenge", Challenge);
                        startActivity(uploadIntent);
                    }
                });

                FloatingActionButton accomplishmentsButton =
                        (FloatingActionButton) findViewById(R.id.accomplishments_button);
                accomplishmentsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CoordinatorUtil.toFeed(context, Challenge);
                    }
                });

                ImageView imageView = (ImageView) findViewById(R.id.image);
                String url = ImageUtil.getUrl(Challenge.image);
                Glide.with(context)
                        .load(url)
                        .listener(new ProgressController(imageProgressView))
                        .into(imageView);

                if (!NetworkUtil.connectionCheck(context, ChallengeView)) {
                    uploadButton.setVisibility(View.GONE);
                    accomplishmentsButton.setVisibility(View.GONE);
                }
            } else {
                // TODO: Show error
            }
        }
    };

}
