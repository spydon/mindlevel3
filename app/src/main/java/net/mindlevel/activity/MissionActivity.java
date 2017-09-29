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

import net.mindlevel.CoordinatorActivity;
import net.mindlevel.util.CoordinatorUtil;
import net.mindlevel.util.ProgressController;
import net.mindlevel.R;
import net.mindlevel.api.ControllerCallback;
import net.mindlevel.api.MissionController;
import net.mindlevel.model.Mission;
import net.mindlevel.util.ImageUtil;
import net.mindlevel.util.NetworkUtil;

public class MissionActivity extends AppCompatActivity {

    private MissionController controller;
    private View missionView, progressView;
    private ProgressBar imageProgressView;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission);
        missionView = findViewById(R.id.mission_content);
        progressView = findViewById(R.id.progress);
        imageProgressView = (ProgressBar) findViewById(R.id.image_progress);
        controller = new MissionController(missionView.getContext());
        context = this;

        showProgress(true);
        if(getIntent().hasExtra("mission")) {
            Mission mission = (Mission) getIntent().getSerializableExtra("mission");
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle(mission.title);
            missionCallback.onPostExecute(true, mission);
        } else if(getIntent().hasExtra("missionId")) {
            int missionId = getIntent().getIntExtra("missionId", -1);
            controller.get(missionId, missionCallback);
        }
    }

    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        missionView.setVisibility(show ? View.GONE : View.VISIBLE);
        missionView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                missionView.setVisibility(show ? View.GONE : View.VISIBLE);
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

    private ControllerCallback<Mission> missionCallback = new ControllerCallback<Mission>() {

        @Override
        public void onPostExecute(final Boolean success, final Mission mission) {
            showProgress(false);

            if (success) {
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                toolbar.setTitle(mission.title);

                TextView titleView = (TextView) findViewById(R.id.title);
                titleView.setText(mission.title);

                TextView descriptionView = (TextView) findViewById(R.id.description);
                descriptionView.setText(mission.description);

                TextView creatorView = (TextView) findViewById(R.id.creator);
                creatorView.setText(mission.creator);

                creatorView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CoordinatorUtil.toUser(context, mission.creator);
                    }
                });

                FloatingActionButton uploadButton = (FloatingActionButton) findViewById(R.id.upload_button);
                uploadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent uploadIntent = new Intent(context, UploadActivity.class);
                        uploadIntent.putExtra("mission", mission);
                        startActivity(uploadIntent);
                    }
                });

                FloatingActionButton accomplishmentsButton =
                        (FloatingActionButton) findViewById(R.id.accomplishments_button);
                accomplishmentsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CoordinatorUtil.toFeed(context, mission.id);
                    }
                });

                ImageView imageView = (ImageView) findViewById(R.id.image);
                String url = ImageUtil.getUrl(mission.image);
                Glide.with(context)
                        .load(url)
                        .listener(new ProgressController(imageProgressView))
                        .into(imageView);

                if(!NetworkUtil.connectionCheck(context, missionView)) {
                    uploadButton.setVisibility(View.GONE);
                }
            } else {
                // TODO: Show error
            }
        }
    };

}
