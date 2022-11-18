package com.fullybelly.views.animation;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Transformation;

public final class ShowHideAnimation {

    //region public methods

    public static void show(final View v, int widthMeasureSpec, final int heightMeasureSpec) {
        Animation heightAnimation = increaseHeight(v, widthMeasureSpec, heightMeasureSpec);
        Animation alphaAnimation = increaseAlpha((int)heightAnimation.getDuration());
        AnimationSet animations = new AnimationSet(true);

        animations.setFillEnabled(true);
        animations.addAnimation(heightAnimation);
        animations.addAnimation(alphaAnimation);

        v.startAnimation(animations);
    }

    public static void hide(final View v) {
        Animation heightAnimation = decreaseHeight(v);
        Animation alphaAnimation = decreaseAlpha((int)heightAnimation.getDuration());
        AnimationSet animations = new AnimationSet(true);

        animations.setFillEnabled(true);
        animations.addAnimation(heightAnimation);
        animations.addAnimation(alphaAnimation);

        v.startAnimation(animations);
    }

    //endregion

    //region private methods

    private static Animation increaseHeight(final View v, int widthMeasureSpec, final int heightMeasureSpec) {
        v.measure(widthMeasureSpec, heightMeasureSpec);

        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? heightMeasureSpec
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        int duration = (int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density);

        a.setDuration(duration * 2);

        return a;
    }

    private static Animation increaseAlpha(int duration) {
        AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(duration);

        return animation;
    }

    private static Animation decreaseHeight(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        int duration = (int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density);

        a.setDuration(duration * 2);

        return a;
    }

    private static Animation decreaseAlpha(int duration) {
        AlphaAnimation animation = new AlphaAnimation(1.0f, 0.0f);
        animation.setDuration(duration);

        return animation;
    }

    //endregion

}
