package net.mindlevel.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.mindlevel.view.ImageLikeView;
import net.mindlevel.util.ProgressController;
import net.mindlevel.R;
import net.mindlevel.model.Accomplishment;
import net.mindlevel.util.ImageUtil;

public class AccomplishmentActivity extends AppCompatActivity {

    private ImageLikeView imageView;
    private Activity activity;
    private ProgressBar progressBar;

    private final int SHARE = 1;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accomplishment);
        this.activity = this;
        final Accomplishment accomplishment = (Accomplishment) getIntent().getSerializableExtra("accomplishment");
        final String url = ImageUtil.getUrl(accomplishment.image);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(accomplishment.title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton missionButton = (FloatingActionButton) findViewById(R.id.fab_mission);
        missionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent missionIntent = new Intent(activity, MissionActivity.class);
                missionIntent.putExtra("missionId", accomplishment.missionId);
                startActivity(missionIntent);
            }
        });

        final FloatingActionButton shareButton = (FloatingActionButton) findViewById(R.id.fab_share);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("image/*");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, accomplishment.title);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, accomplishment.description);
                Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "", null);
                Uri uri = Uri.parse(path);
                sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
                activity.startActivityForResult(Intent.createChooser(sharingIntent, "Share using"), SHARE);
            }
        });

        TextView imageText = (TextView) findViewById(R.id.image_text);
        imageView = (ImageLikeView) findViewById(R.id.image);
        imageView.setTextView(imageText);
        imageView.setId(accomplishment.id);

        progressBar = (ProgressBar) findViewById(R.id.progress);
        Glide.with(this)
                .load(url)
                .listener(new ProgressController(progressBar))
                .into(imageView);

        TextView titleView = (TextView) findViewById(R.id.title);
        TextView scoreView = (TextView) findViewById(R.id.score);
        TextView descriptionView = (TextView) findViewById(R.id.description);
        titleView.setText(accomplishment.title);
        scoreView.setText(Integer.toString(accomplishment.score));
        descriptionView.setText(accomplishment.description);

        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();

    }
}
