package net.mindlevel.fragment;

// TODO: Change back to non-support lib

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import net.mindlevel.R;

import java.util.Random;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public abstract class InfoFragment extends Fragment {

    protected int shortAnimTime;
    protected View contentView, progressView, errorView;

    protected void showInfo(boolean isError, boolean isProgress) {
        showInfo(isError, isProgress, null);
    }

    protected void showInfo(boolean isError, boolean isProgress, String message) {
        if(!isAdded()) {
            return;
        }

        TextView errorText = (TextView)errorView.findViewById(R.id.error_text);
        TextView progressText = (TextView)progressView.findViewById(R.id.progress_text);

        if(message == null) {
            errorText.setText(R.string.error_network);
            progressText.setText(getRandomMotivation());
        } else {
            errorText.setText(message);
            progressText.setText(message);
        }

        final boolean isNormal = !isError && !isProgress;
        if(isNormal) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    animateToFront(contentView);
                }
            }, 1000);
        } else if(isError) {
            animateToFront(errorView);
        } else if(isProgress) {
            animateToFront(progressView);
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

    private void animateToFront(View view) {
        if(isAdded()) {
            View[] views = {contentView, progressView, errorView};
            for (View other : views) {
                if (view != other) {
                    other.setVisibility(GONE);
                }
            }
            view.setVisibility(VISIBLE);
            view.setAlpha(0F);
            view.animate().setDuration(shortAnimTime).alpha(1F).setListener(null);
        }
    }
}
