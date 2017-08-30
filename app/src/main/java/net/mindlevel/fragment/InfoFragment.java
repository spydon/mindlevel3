package net.mindlevel.fragment;

// TODO: Change back to non-support lib

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
        contentView.animate().setDuration(shortAnimTime).alpha(
                isNormal ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                contentView.setVisibility(isNormal ? VISIBLE : GONE);
            }
        });

        errorView.setVisibility(GONE);
        progressView.setVisibility(GONE);
        //errorView.setVisibility(isError ? View.VISIBLE : View.GONE);
        //errorView.animate().setDuration(shortAnimTime).alpha(
        //        isError ? 1 : 0).setListener(new AnimatorListenerAdapter() {
        //    @Override
        //    public void onAnimationEnd(Animator animation) {
        //        progressView.setVisibility(isError ? View.VISIBLE : View.GONE);
        //    }
        //});

        //progressView.setVisibility(isProgress ? View.VISIBLE : View.GONE);
        //progressView.animate().setDuration(shortAnimTime).alpha(
        //        isProgress ? 1 : 0).setListener(new AnimatorListenerAdapter() {
        //    @Override
        //    public void onAnimationEnd(Animator animation) {
        //        progressView.setVisibility(isProgress ? View.VISIBLE : View.GONE);
        //    }
        //});
    }
}