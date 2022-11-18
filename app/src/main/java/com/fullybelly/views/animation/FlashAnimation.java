package com.fullybelly.views.animation;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

public final class FlashAnimation {

    //region public methods

    public static void flash(final View v) {
        final Animation animation = new AlphaAnimation(1f, 0.3f);

        animation.setDuration(200);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);
        v.startAnimation(animation);
    }

    //endregion

}
