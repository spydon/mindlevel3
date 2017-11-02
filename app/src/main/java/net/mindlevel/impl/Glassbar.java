package net.mindlevel.impl;

import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;

import net.mindlevel.R;

/**
 * Can not inherit from Snackbar since snackbar is final
 */

public class Glassbar {
    public static Snackbar make(@NonNull View view, @NonNull CharSequence text, @BaseTransientBottomBar.Duration int duration) {
        Snackbar bar = Snackbar.make(view, text, duration);
        bar.getView().setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.snackbar));
        return bar;
    }
}
