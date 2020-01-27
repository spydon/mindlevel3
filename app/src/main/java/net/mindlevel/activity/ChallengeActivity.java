package net.mindlevel.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

public class ChallengeActivity extends InfoActivity {

    private ChallengeController controller;
    private ProgressBar imageProgressView;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);

        // For the info center
        initializeViews();

        imageProgressView = findViewById(R.id.image_progress);
        controller = new ChallengeController(contentView.getContext());
        context = this;

        showInfo(false, true);
        if (getIntent().hasExtra("challenge")) {
            Challenge challenge = (Challenge) getIntent().getSerializableExtra("challenge");
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(challenge.title);
            challengeCallback.onPostExecute(true, challenge);
        } else if (getIntent().hasExtra("challenge_id")) {
            int challengeId = getIntent().getIntExtra("challenge_id", -1);
            controller.get(challengeId, challengeCallback);
        }
    }

    private ControllerCallback<Challenge> challengeCallback = new ControllerCallback<Challenge>() {

        @Override
        public void onPostExecute(final Boolean success, final Challenge challenge) {
            if (success || challenge != null) {
                showInfo(false, false);
                Toolbar toolbar = findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle(challenge.title);

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
                FloatingActionButton accomplishmentsButton = findViewById(R.id.accomplishments_button);
                if (success) {
                    uploadButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent uploadIntent = new Intent(context, UploadActivity.class);
                            uploadIntent.putExtra("challenge", challenge);
                            startActivity(uploadIntent);
                        }
                    });

                    accomplishmentsButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CoordinatorUtil.toFeed(context, challenge);
                        }
                    });
                } else {
                    uploadButton.setVisibility(View.GONE);
                    accomplishmentsButton.setVisibility(View.GONE);
                }

                if (!NetworkUtil.connectionCheck(context, contentView)) {
                    uploadButton.setVisibility(View.GONE);
                    accomplishmentsButton.setVisibility(View.GONE);
                }

                ImageView imageView = findViewById(R.id.image);
                String url = ImageUtil.getUrl(challenge.image);
                Glide.with(context)
                        .load(url)
                        .listener(new ProgressController(imageProgressView))
                        .into(imageView);

            } else {
                showInfo(true, false);
            }
        }
    };

}
