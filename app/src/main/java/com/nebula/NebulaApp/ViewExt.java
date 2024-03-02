package com.nebula.NebulaApp;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class ViewExt extends AnimationUtils {
    public static void startAnimation(View view, Animation animation, final Runnable onEnd) {
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Do nothing
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                onEnd.run();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Do nothing
            }
        });
        view.startAnimation(animation);
    }
}

