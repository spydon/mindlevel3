package net.mindlevel.view;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.mindlevel.R;
import net.mindlevel.api.AccomplishmentController;
import net.mindlevel.api.ControllerCallback;
import net.mindlevel.model.Like;

public class ImageLikeView extends AppCompatImageView {
    private GestureListener gestureListener;
    private GestureDetector gestureDetector;
    private Context context;

    private OnClickListener tapListener;
    private View tapSource;
    private ProgressBar progress;
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
        controller = new AccomplishmentController(context);
        gestureListener = new GestureListener();
        gestureDetector = new GestureDetector(context, gestureListener, null, true);
        setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                invalidate();
                return true;
            }

        });
    }

    public void setTextView(TextView imageText) {
        this.imageText = imageText;
    }
    public void setProgressLike(ProgressBar progress) {
        this.progress = progress;
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
            final TextView likeText = imageText;
            if(likeText == null) {
                return false;
            }
            progress.setVisibility(VISIBLE);

            ControllerCallback<Like> callback = new ControllerCallback<Like>() {
                @Override
                public void onPostExecute(Boolean isSuccess, final Like like) {
                    int duration = 1500;
                    final AlphaAnimation countAnim = new AlphaAnimation(1.0f, 0.0f);
                    countAnim.setDuration(duration);
                    countAnim.setRepeatMode(Animation.REVERSE);
                    countAnim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            likeText.setVisibility(VISIBLE);
                        }
                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            likeText.setVisibility(GONE);
                            likeText.setText(R.string.action_like);
                        }
                    });

                    AlphaAnimation likeAnim = new AlphaAnimation(1.0f, 0.0f);
                    likeAnim.setDuration(duration);
                    likeAnim.setRepeatMode(Animation.REVERSE);
                    likeAnim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            likeText.setVisibility(VISIBLE);
                        }
                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            likeText.setText(like.score);
                            likeText.startAnimation(countAnim);
                        }
                    });

                    final AlphaAnimation progressAnim = new AlphaAnimation(1.0f, 0.0f);
                    progressAnim.setDuration(duration/3);
                    progressAnim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {}
                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            progress.setVisibility(GONE);
                        }
                    });

                    progress.startAnimation(progressAnim);
                    if(isSuccess && like.first) {
                        likeText.startAnimation(likeAnim);
                    } else if(like != null) {
                        likeText.setText(like.score);
                        likeText.startAnimation(countAnim);
                    }
                }
            };

            controller.like(id, callback);
            return true;
        }
    }

}
