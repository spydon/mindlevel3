package net.mindlevel;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.mindlevel.model.Mission;

public class MissionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission);
        final Mission mission = (Mission) getIntent().getSerializableExtra("mission");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(mission.title);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, mission.title, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageView imageView = (ImageView) findViewById(R.id.mission_image);
        Glide.with(this).load(mission.imageUrl).into(imageView);

        TextView titleView = (TextView) findViewById(R.id.mission_title);
        TextView descriptionView = (TextView) findViewById(R.id.mission_description);
        titleView.setText(mission.title);
        descriptionView.setText(mission.description);
    }

}
