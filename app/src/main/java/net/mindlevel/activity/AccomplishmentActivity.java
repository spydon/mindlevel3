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
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import net.mindlevel.R;
import net.mindlevel.api.AccomplishmentController;
import net.mindlevel.api.ControllerCallback;
import net.mindlevel.fragment.ContributorRecyclerViewAdapter;
import net.mindlevel.impl.Glassbar;
import net.mindlevel.impl.ImageLikeView;
import net.mindlevel.impl.ProgressController;
import net.mindlevel.model.Accomplishment;
import net.mindlevel.model.Level;
import net.mindlevel.model.User;
import net.mindlevel.util.ImageUtil;
import net.mindlevel.util.PermissionUtil;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static android.view.View.GONE;
import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class AccomplishmentActivity extends AppCompatActivity {

    private static final int SHARE_PERMISSION = 101;
    private View coordinator;
    private ImageLikeView imageView;
    private Activity activity;
    private ContributorRecyclerViewAdapter adapter;
    private View contributorProgress;
    private List<User> contributors;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accomplishment);
        Context context = getBaseContext();
        this.activity = this;
        this.coordinator = findViewById(R.id.coordinator);
        this.contributors = new ArrayList<>();
        this.adapter = new ContributorRecyclerViewAdapter(activity, contributors);
        final Accomplishment accomplishment = (Accomplishment) getIntent().getSerializableExtra("accomplishment");
        final String url = ImageUtil.getUrl(accomplishment.image);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(accomplishment.title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.contributorProgress = findViewById(R.id.contributor_progress);
        FloatingActionButton challengeButton = findViewById(R.id.fab_challenge);
        challengeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent challengeIntent = new Intent(activity, ChallengeActivity.class);
                challengeIntent.putExtra("challenge_id", accomplishment.challengeId);
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

        TextView imageText = findViewById(R.id.image_text);
        ProgressBar likeProgress = findViewById(R.id.progress_like);
        this.imageView = findViewById(R.id.image);
        imageView.setTextView(imageText);
        imageView.setProgressLike(likeProgress);
        imageView.setId(accomplishment.id);

        final View backgroundView = findViewById(R.id.scroll);

        ProgressBar progressBar = findViewById(R.id.progress);
        Glide.with(this)
                .load(url)
                .listener(new ProgressController(progressBar))
                .into(imageView);

        Glide.with(this)
                .load(url)
                .apply(bitmapTransform(new BlurTransformation(25, 3)))
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        resource.setAlpha(160);
                        backgroundView.setBackground(resource);
                    }
                });

        TextView titleView = findViewById(R.id.title);
        TextView scoreView = findViewById(R.id.score);
        TextView levelView = findViewById(R.id.level);
        TextView descriptionView = findViewById(R.id.description);
        titleView.setText(accomplishment.title);
        String scoreText = context.getString(R.string.title_score, String.valueOf(accomplishment.score));
        scoreView.setText(scoreText);
        Level level = new Level(accomplishment.levelRestriction);
        String levelText = context.getString(R.string.title_level, level.getVisualLevel());
        levelView.setText(levelText);
        descriptionView.setText(accomplishment.description);

        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();

        AccomplishmentController controller = new AccomplishmentController(this);
        controller.getContributors(accomplishment.id, contributorsCallback);

        RecyclerView recyclerView = findViewById(R.id.contributors);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
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
            super.onRequestPermissionsResult(requestCode, permissions,
                    grantResults);
        }
    }

    private ControllerCallback<List<User>> contributorsCallback = new ControllerCallback<List<User>>() {
        @Override
        public void onPostExecute(Boolean isSuccess, List<User> response) {
            contributorProgress.setVisibility(GONE);
            if (isSuccess) {
                contributors.clear();
                contributors.addAll(response);
                adapter.notifyDataSetChanged();
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
