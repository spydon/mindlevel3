package net.mindlevel;

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

import net.mindlevel.api.ControllerCallback;
import net.mindlevel.api.MissionController;
import net.mindlevel.model.Mission;

public class MissionActivity extends AppCompatActivity {

    private MissionController controller;
    private View missionView;
    private ProgressBar progressView, imageProgressView;
    private Context outerContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission);
        missionView = findViewById(R.id.mission_content);
        progressView = (ProgressBar) findViewById(R.id.progress);
        imageProgressView = (ProgressBar) findViewById(R.id.image_progress);
        controller = new MissionController(missionView.getContext());
        outerContext = this;
        showProgress(true);

        if(getIntent().hasExtra("mission")) {
            Mission mission = (Mission) getIntent().getSerializableExtra("mission");
            missionCallback.onPostExecute(true, mission);
        } else {
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

                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // TODO: move snackbar
                        //Snackbar.make(view, mission.title, Snackbar.LENGTH_LONG)
                        //        .setAction("Action", null).show();

                        Intent missionIntent = new Intent(outerContext, UploadActivity.class);
                        missionIntent.putExtra("mission", mission);
                        startActivity(missionIntent);
                    }
                });

                ImageView imageView = (ImageView) findViewById(R.id.image);
                String url = ImageUtil.getUrl(mission.image);
                Glide.with(outerContext)
                        .load(url)
                        .listener(new ProgressBarController(imageProgressView))
                        .into(imageView);
            } else {
                // TODO: Show error
            }
        }
    };

}
