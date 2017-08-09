package net.mindlevel;


import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

public class ImageLikeView extends AppCompatImageView {
    private GestureListener gestureListener;
    private GestureDetector gestureDetector;
    private Context context;

    private OnClickListener tapListener;
    private View tapSource;
    private TextView imageText;

    public ImageLikeView(Context context) {
        super(context);
        sharedConstructing(context);
    }

    public ImageLikeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        sharedConstructing(context);
    }

    public ImageLikeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        sharedConstructing(context);
    }

    private void sharedConstructing(Context context) {
        super.setClickable(true);
        this.context = context;
        // TODO: Investigate whether this should be removed or if setTextView pattern should be refactored
        this.imageText =  (TextView) ((Activity) context).findViewById(R.id.image_text);
        gestureListener = new GestureListener();
        gestureDetector = new GestureDetector(context, gestureListener, null, true);
        setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                invalidate();
                return true; // indicate event was handled
            }

        });
    }

    public void setTextView(TextView imageText) {
        this.imageText = imageText;
    }

    public void setClickListener(OnClickListener tapListener, View tapSource) {
        this.tapListener = tapListener;
        this.tapSource = tapSource;
    }

    public class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if(tapListener != null){
                tapListener.onClick(tapSource);
            }

            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            final TextView view = imageText;
            if(view == null) {
                return false;
            }

            view.setVisibility(VISIBLE);
            int duration = 1500;
            AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
            anim.setDuration(duration);
            anim.setRepeatMode(Animation.REVERSE);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}
                @Override
                public void onAnimationRepeat(Animation animation) {}
                @Override
                public void onAnimationEnd(Animation animation) { view.setVisibility(GONE); }
            });
            view.startAnimation(anim);
            return true;
        }
    }

}
