package net.mindlevel.fragment;

// TODO: Change back to non-support lib

import android.support.v4.app.Fragment;
import android.view.View;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public abstract class InfoFragment extends Fragment {

    protected int shortAnimTime;
    protected View contentView, progressView, errorView;

    protected void showInfo(final boolean isError, final boolean isProgress) {
        final boolean isNormal = !isError && !isProgress;
        contentView.setVisibility(isNormal ? VISIBLE : GONE);
        errorView.setVisibility(GONE);
        progressView.setVisibility(GONE);
    }
}
