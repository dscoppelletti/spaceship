/*
 * Copyright 2015 The Android Open Source Project
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
 *
 * - Dario Scoppelletti, 2017
 * Repository: http://github.com/ianhanniballake/cheesesquare
 * File: app/src/main/java/com/support/android/designlibdemo/
 *       FABAwareScrollingViewBehavior.java
 * Commit: 5a5c3b29ae684e3a840b095bfca8d90a5a79af46 - January 13, 2017
 * Replace deprecated methods.
 * Suppress warnings.
 * Add Javadoc.
 * Porting to Android X and Material Design Components.
 * Upgrade to androidx.coordinatorlayout:coordinatorlayout:1.1.0.
 *
 * - Genymotion 2.11.0
 * FAB never hides likely because "pixel perfect" is disabled.
 *
 * - Dario Scoppelletti, 2022
 * Handle extended FABs, too.
 */
package it.scoppelletti.spaceship.widget;

import java.util.List;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Behavior for animating a {@code FloatingActionButton} when the user scrolls
 * the view.
 *
 * <p>When the user scrolls down a view, the {@code FloatingActionButton} should
 * disappear; once the view scrolls to the top, it should reappear.</p>
 *
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class FABAwareScrollingViewBehavior extends
        AppBarLayout.ScrollingViewBehavior {

    /**
     * Constructor.
     *
     * @param context Context.
     * @param attrs   Attributes.
     */
    public FABAwareScrollingViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child,
            View dependency) {
        // Original implementation does not handle extended FABs
        return dependency instanceof FloatingActionButton ||
                dependency instanceof ExtendedFloatingActionButton ||
                super.layoutDependsOn(parent, child, dependency);
    }

    @Override
    public boolean onStartNestedScroll(
            @NonNull CoordinatorLayout coordinatorLayout, @NonNull View child,
            @NonNull View directTargetChild, @NonNull View target, int axes,
            int type) {
        // Ensure we react to vertical scrolling
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL ||
                super.onStartNestedScroll(coordinatorLayout, child,
                        directTargetChild, target, axes, type);
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout,
            @NonNull View child, @NonNull View target, int dxConsumed,
            int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type,
            @NonNull int[] consumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed,
                dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed);
        // Original implementation handles all the dependencies which are
        // FAB but not extended FAB
        if (dyConsumed > 0) {
            // User scrolled down -> hide the FAB
            List<View> dependencies = coordinatorLayout.getDependencies(child);
            for (View view : dependencies) {
                if (view instanceof FloatingActionButton) {
                    ((FloatingActionButton) view).hide();
                }
                if (view instanceof ExtendedFloatingActionButton) {
                    ((ExtendedFloatingActionButton) view).hide();
                }
            }
        } else if (dyConsumed < 0) {
            // User scrolled up -> show the FAB
            List<View> dependencies = coordinatorLayout.getDependencies(child);
            for (View view : dependencies) {
                if (view instanceof FloatingActionButton) {
                    ((FloatingActionButton) view).show();
                }
                if (view instanceof ExtendedFloatingActionButton) {
                    ((ExtendedFloatingActionButton) view).show();
                }
            }
        }
    }
}
