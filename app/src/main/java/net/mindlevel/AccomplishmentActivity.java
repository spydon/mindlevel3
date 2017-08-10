package net.mindlevel;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;

import net.mindlevel.model.Accomplishment;

import static android.view.View.GONE;

public class AccomplishmentActivity extends AppCompatActivity {

    private ImageLikeView imageView;
    private Context context;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accomplishment);
        this.context = this;
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

        TextView imageText = (TextView) findViewById(R.id.image_text);
        imageView = (ImageLikeView) findViewById(R.id.image);
        imageView.setTextView(imageText);
        imageView.setId(accomplishment.id);

        progressBar = (ProgressBar) findViewById(R.id.progress);
        String url = ImageUtil.getUrl(accomplishment.image);
        Glide.with(this)
                .load(url)
                //.listener(new ProgressBarController(progressBar))
                .listener(shareLoading)
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

    private RequestListener shareLoading = new RequestListener() {
        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
            progressBar.setVisibility(GONE);
            return false;
        }

        @Override
        public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
            progressBar.setVisibility(GONE);
            ShareButton shareButton = (ShareButton) findViewById(R.id.fb_share_button);
            shareButton.setEnabled(true);
            Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
            SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(bitmap)
                    .build();
            SharePhotoContent content = new SharePhotoContent.Builder()
                    .addPhoto(photo)
                    .build();

            shareButton.setShareContent(content);
            return false;
        }
    };
}
