package net.mindlevel.activity;

// TODO: Change back to non-support lib

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.mindlevel.R;
import net.mindlevel.model.User;
import net.mindlevel.util.ImageUtil;
import net.mindlevel.util.ProgressController;

public class UserActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView usernameView;
    private TextView scoreView;
    private TextView descriptionView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        View view = findViewById(R.id.content);

        User user = (User) getIntent().getSerializableExtra("user");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(user.username);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.imageView = (ImageView) view.findViewById(R.id.image);
        this.usernameView = (TextView) view.findViewById(R.id.username);
        this.scoreView = (TextView) view.findViewById(R.id.score);
        this.descriptionView = (TextView) view.findViewById(R.id.description);
        this.progressBar = (ProgressBar) findViewById(R.id.progress_image);

        usernameView.setText(user.username);
        scoreView.setText(String.valueOf(user.score));
        descriptionView.setText(user.description);
        if(user.image != null && !user.image.isEmpty()) {
            String url = ImageUtil.getUrl(user.image);
            Glide.with(this)
                    .load(url)
                    .listener(new ProgressController(progressBar))
                    .into(imageView);
        }
    }

}
