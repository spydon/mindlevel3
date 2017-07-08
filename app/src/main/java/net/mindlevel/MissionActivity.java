package net.mindlevel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
        final Context outerContext = this;
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageView imageView = (ImageView) findViewById(R.id.image);
        Glide.with(this).load(mission.imageUrl).into(imageView);

        TextView titleView = (TextView) findViewById(R.id.title);
        TextView descriptionView = (TextView) findViewById(R.id.description);
        titleView.setText(mission.title);
        descriptionView.setText(mission.description);
    }

}
