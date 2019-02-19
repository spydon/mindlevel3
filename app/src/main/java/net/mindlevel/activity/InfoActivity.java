package net.mindlevel.activity;

// TODO: Change back to non-support lib

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import net.mindlevel.R;

import java.util.Random;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public abstract class InfoActivity extends AppCompatActivity {

    protected int shortAnimTime;
    protected View infoView, contentView, progressView, errorView;

    protected void initializeViews() {
        View rootView = findViewById(android.R.id.content);
        infoView = rootView.findViewById(R.id.info_center);
        contentView = rootView.findViewById(R.id.content);
        progressView = rootView.findViewById(R.id.progress);
        errorView = rootView.findViewById(R.id.error);
    }

    protected void showInfo(boolean isError, boolean isProgress) {
        showInfo(isError, isProgress, null);
    }

    protected void showInfo(boolean isError, boolean isProgress, String message) {
        TextView errorText = errorView.findViewById(R.id.error_text);
        TextView progressText = progressView.findViewById(R.id.progress_text);

        final boolean isNormal = !isError && !isProgress;
        if (isNormal) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    animateToFront(contentView, false);
                }
            }, 500);
        } else if (isError) {
            String errorMessage = message == null ? getString(R.string.error_network) : message;
            errorText.setText(errorMessage);
            animateToFront(errorView, true);
        } else if (isProgress) {
            String progressMessage = message == null ? getRandomMotivation() : message;
            progressText.setText(progressMessage);
            animateToFront(progressView, true);
        }
    }

    private String getRandomMotivation() {
        String[] motivations = {
                getString(R.string.progress_order),
                getString(R.string.progress_petting),
                getString(R.string.progress_satelite),
                getString(R.string.progress_sleep)
        };
        return motivations[new Random().nextInt(motivations.length)];
    }

    private void animateToFront(View view, boolean isInfo) {
        View[] views = {contentView, progressView, errorView};
        for (View other : views) {
            if (view != other) {
                other.setVisibility(GONE);
            }
        }
        view.setVisibility(VISIBLE);

        if (!isInfo) {
            infoView.setVisibility(GONE);
        }
        View frontView = isInfo ? infoView : view;
        frontView.setAlpha(0);
        frontView.setVisibility(VISIBLE);
        frontView.animate().setDuration(shortAnimTime).alpha(1);
    }
}
