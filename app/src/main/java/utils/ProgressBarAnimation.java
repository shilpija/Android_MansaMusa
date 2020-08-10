package utils;

import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ProgressBar;

/**
 * Created by mobua01 on 31/7/17.
 */

public class ProgressBarAnimation extends Animation {
    private ProgressBar progressBar;
    private float from;
    private float  to;

//    private MyInterface listener;
//
//    public MyInterface getListener() {
//        return listener;
//    }
//
//    public void setListener(MyInterface listener) {
//        this.listener = listener;
//    }

    public ProgressBarAnimation(ProgressBar progressBar, float from, float to) {
        super();
        this.progressBar = progressBar;
        this.from = from;
        this.to = to;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        float value = from + (to - from) * interpolatedTime;
        progressBar.setProgress((int) value);
    }


//    public class MyAnimListener implements AnimationListener {
//
//        @Override
//        public void onAnimationStart(Animation animation) {
//            ProgressBarAnimation.this.getListener().onAnimationStart(animation);
//        }
//
//        @Override
//        public void onAnimationEnd(Animation animation) {
//            ProgressBarAnimation.this.getListener().onAnimationEnd(animation);
//
//        }
//
//        @Override
//        public void onAnimationRepeat(Animation animation) {
//            ProgressBarAnimation.this.getListener().onAnimationRepeat(animation);
//
//        }
//    }
//
//    public interface MyInterface
//    {
//        void onAnimationStart(Animation animation);
//        void onAnimationEnd(Animation animation);
//        void onAnimationRepeat(Animation animation);
//    }



}