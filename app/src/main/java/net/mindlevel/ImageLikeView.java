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

import net.mindlevel.api.AccomplishmentController;
import net.mindlevel.api.ControllerCallback;

public class ImageLikeView extends AppCompatImageView {
    private GestureListener gestureListener;
    private GestureDetector gestureDetector;
    private Context context;

    private OnClickListener tapListener;
    private View tapSource;
    private TextView imageText;

    private AccomplishmentController controller;
    private int id;

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
        controller = new AccomplishmentController(context);
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
    public void setId(int id) { this.id = id; }

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


            ControllerCallback<String> callback = new ControllerCallback<String>() {
                @Override
                public void onPostExecute(Boolean isSuccess, final String response) {
                    int duration = 1500;
                    final AlphaAnimation countAnim = new AlphaAnimation(1.0f, 0.0f);
                    countAnim.setDuration(duration);
                    countAnim.setRepeatMode(Animation.REVERSE);
                    countAnim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            view.setText(response);
                            view.setVisibility(VISIBLE);
                        }
                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            view.setVisibility(GONE);
                            view.setText(R.string.action_like);
                        }
                    });

                    AlphaAnimation likeAnim = new AlphaAnimation(1.0f, 0.0f);
                    likeAnim.setDuration(duration);
                    likeAnim.setRepeatMode(Animation.REVERSE);
                    likeAnim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) { view.setVisibility(VISIBLE); }
                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            view.setText(response);
                            view.startAnimation(countAnim);
                        }
                    });

                    if(isSuccess) {
                        view.startAnimation(likeAnim);
                    } else if(response != null) {
                        view.startAnimation(countAnim);
                    }
                }
            };

            controller.like(id, callback);
            return true;
        }
    }

}
