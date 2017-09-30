package net.mindlevel.fragment;

// TODO: Change back to non-support lib

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.v4.app.Fragment;
import android.view.View;

import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public abstract class InfoFragment extends Fragment {

    protected int shortAnimTime;
    protected View contentView, progressView, errorView;

    protected void showInfo(boolean isError, boolean isProgress) {
        final boolean isNormal = !isError && !isProgress;

        if(isNormal) {
            animateToFront(contentView);
        } else if(isError) {
            animateToFront(errorView);
        } else if(isProgress) {
            animateToFront(progressView);
        }
    }

    private void animateToFront(View view) {
        View[] views = {contentView, progressView, errorView};
        for(View other : views) {
            if(view != other) {
                other.setVisibility(GONE);
            }
        }
        view.setVisibility(VISIBLE);
        view.setAlpha(0F);
        view.animate().setDuration(shortAnimTime).alpha(1F).setListener(null);
    }
}
