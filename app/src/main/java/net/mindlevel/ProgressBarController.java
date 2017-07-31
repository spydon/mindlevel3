package net.mindlevel;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ProgressBar;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class ProgressBarController implements RequestListener<Drawable> {
    private final ProgressBar progressBar;

    public ProgressBarController(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public void hide() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target,
                                boolean isFirstResource) {
        hide();
        return false;
    }

    @Override
    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
                                   DataSource dataSource, boolean isFirstResource) {
        hide();
        return false;
    }
}
