package net.mindlevel;

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

import net.mindlevel.model.Accomplishment;

public class AccomplishmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accomplishment);
        final Accomplishment accomplishment = (Accomplishment) getIntent().getSerializableExtra("accomplishment");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(accomplishment.title);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final Context outerContext = this;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent missionIntent = new Intent(outerContext, MissionActivity.class);
                missionIntent.putExtra("missionId", accomplishment.missionId);
                startActivity(missionIntent);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageView imageView = (ImageView) findViewById(R.id.image);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress);
        Glide.with(this)
                .load(accomplishment.image)
                .listener(new ProgressBarController(progressBar))
                .into(imageView);

        TextView titleView = (TextView) findViewById(R.id.title);
        TextView descriptionView = (TextView) findViewById(R.id.description);
        titleView.setText(accomplishment.title);
        descriptionView.setText(accomplishment.description);
    }

}
