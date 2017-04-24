/*
 * Copyright (C) 2017 Dario Scoppelletti, <http://www.scoppelletti.it/>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.scoppelletti.spaceship.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import lombok.extern.slf4j.Slf4j;

/**
 * Circular indeterminate progress indicator within a screen overlay.
 *
 * @since 1.0.0
 */
@Slf4j
public final class ProgressOverlay extends CompoundControl {
    private static final int ALPHA_GONE = 0;
    private static final int ALPHA_VISIBLE = 102;
    private static final long DELAY = 400L;
    private static final long DURATION = 400L;
    private static final String PROP_RUNNING = "1";
    private ProgressBar myIndicator;
    private ValueAnimator myShowAnim;
    private ValueAnimator myHideAnim;

    /**
     * Constructor.
     *
     * @param ctx The context.
     */
    public ProgressOverlay(@NonNull Context ctx) {
        super(ctx);
        init();
    }

    /**
     * Constructor.
     *
     * @param ctx   The context.
     * @param attrs The attributes. May be {@code null}.
     */
    public ProgressOverlay(@NonNull Context ctx,
            @Nullable AttributeSet attrs) {
        super(ctx, attrs);
        init();
    }

    /**
     * Initialization.
     */
    private void init() {
        FrameLayout.LayoutParams layout;

        myIndicator = new ProgressBar(getContext());
        myIndicator.setIndeterminate(true);
        layout = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER);
        addView(myIndicator, layout);

        myShowAnim = initShowAnimator();
        myHideAnim = initHideAnimator();
    }

    /**
     * Creates the animation for showing this progress indicator.
     *
     * @return The new object.
     */
    private ValueAnimator initShowAnimator() {
        ValueAnimator anim;

        anim = ValueAnimator.ofInt(ProgressOverlay.ALPHA_GONE,
                ProgressOverlay.ALPHA_VISIBLE);
        anim.setStartDelay(ProgressOverlay.DELAY);
        anim.setDuration(ProgressOverlay.DURATION);

        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int color;
                float f;

                f = animation.getAnimatedFraction();
                myIndicator.setScaleX(f);
                myIndicator.setScaleY(f);

                color = Color.argb((int) animation.getAnimatedValue(), 0, 0, 0);
                setBackgroundColor(color);
            }
        });

        anim.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                setVisibility(View.VISIBLE);
            }
        });

        return anim;
    }

    /**
     * Creates the animation for hiding this progress indicator.
     *
     * @return The new object.
     */
    private ValueAnimator initHideAnimator() {
        ValueAnimator anim;

        anim = ValueAnimator.ofInt(ProgressOverlay.ALPHA_VISIBLE,
                ProgressOverlay.ALPHA_GONE);
        anim.setDuration(ProgressOverlay.DURATION);

        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int color;
                float f;

                f = 1.0f - animation.getAnimatedFraction();
                myIndicator.setScaleX(f);
                myIndicator.setScaleY(f);

                color = Color.argb((int) animation.getAnimatedValue(), 0, 0, 0);
                setBackgroundColor(color);
            }
        });

        anim.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                setVisibility(View.GONE);
            }
        });

        return anim;
    }

    /**
     * Sets whether this progress indicator is running or not.
     *
     * @param running Whether this progress indicator is running.
     */
    private void setRunning(boolean running) {
        int color;

        if (running) {
            color = Color.argb(ProgressOverlay.ALPHA_VISIBLE, 0, 0, 0);
            setBackgroundColor(color);
            myIndicator.setScaleX(1.0f);
            myIndicator.setScaleY(1.0f);
            setVisibility(View.VISIBLE);
        } else {
            setVisibility(View.GONE);
            color = Color.argb(ProgressOverlay.ALPHA_GONE, 0, 0, 0);
            setBackgroundColor(color);
            myIndicator.setScaleX(0.0f);
            myIndicator.setScaleY(0.0f);
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle state) {
        setRunning(state.getBoolean(PROP_RUNNING, false));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle state) {
        boolean running;

        if (myShowAnim.isStarted()) {
            running = true;
        } else if (myHideAnim.isStarted()) {
            running = false;
        } else {
            running = (getVisibility() == View.VISIBLE);
        }

        state.putBoolean(ProgressOverlay.PROP_RUNNING, running);
    }

    /**
     * Shows this progress indicator.
     */
    public void show() {
        if (myHideAnim.isStarted()) {
            try {
                myHideAnim.cancel();
            } catch (RuntimeException ex) {
                myLogger.error("Failed to cancel animation.", ex);
            }
        } else if (myShowAnim.isStarted() || getVisibility() == View.VISIBLE) {
            // Already shown
            return;
        }

        myShowAnim.start();
    }

    /**
     * Hides this progress indicator.
     */
    public void hide() {
        if (doHide()) {
            myHideAnim.start();
        }
    }

    /**
     * Hides this progress indicator.
     *
     * @param postAction An action to run when the animation finishes.
     */
    public void hide(@NonNull Runnable postAction) {
        if (postAction == null) {
            throw new NullPointerException("Argument postAction is null.");
        }

        if (doHide()) {
            myHideAnim.addListener(createHideListener(postAction));
            myHideAnim.start();
        } else {
            postAction.run();
        }
    }

    /**
     * Hides this progress indicator.
     *
     * @return Returns {@code true} if hide animation should start,
     *                 {@code false} otherwise.
     */
    private boolean doHide() {
        if (myShowAnim.isStarted()) {
            try {
                myShowAnim.cancel();
            } catch (RuntimeException ex) {
                myLogger.error("Failed to cancel animation.", ex);
            }

            return false;
        }

        if (myHideAnim.isStarted() || getVisibility() != View.VISIBLE) {
            // Already hidden
            return false;
        }

        return true;
    }

    /**
     * Creates a listener for the hiding animation.
     *
     * @param  postAction An action to run when the animation finishes.
     * @return            The new object.
     */
    private Animator.AnimatorListener createHideListener(
            final Runnable postAction) {
        return new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                myHideAnim.removeListener(this);
                postAction.run();
            }
        };
    }
}
