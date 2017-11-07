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
        if (!isAdded() || isDetached() || isRemoving()) {
            return;
        }

        TextView errorText = (TextView) errorView.findViewById(R.id.error_text);
        TextView progressText = (TextView) progressView.findViewById(R.id.progress_text);

        final boolean isNormal = !isError && !isProgress;
        if (isNormal) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    animateToFront(contentView);
                }
            }, 500);
        } else if (isError) {
            String errorMessage = message == null ? getString(R.string.error_network) : message;
            errorText.setText(errorMessage);
            animateToFront(errorView);
        } else if (isProgress) {
            String progressMessage = message == null ? getRandomMotivation() : message;
            progressText.setText(progressMessage);
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
        if (isAdded()) {
            View[] views = {contentView, progressView, errorView};
            for (View other : views) {
                if (view != other) {
                    other.setVisibility(GONE);
                }
            }
            view.setAlpha(0);
            view.setVisibility(VISIBLE);
            view.animate().setDuration(shortAnimTime).alpha(1);
        }
    }
}
