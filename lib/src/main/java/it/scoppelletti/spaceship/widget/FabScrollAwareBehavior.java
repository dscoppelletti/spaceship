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

import java.util.List;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

/**
 * Behavior for attaching a <abbr title="Floating Action Button">FAB</abbr> to a
 * vertical-scrolling widget.
 *
 * @see   <a href="http://github.com/codepath/android_guides/wiki/Floating-Action-Buttons"
 *        target="_blank">Floating Action Buttons &gt; Animating the Floating
 *        Action Button</a>
 *        <a href="http://github.com/ianhanniballake/cheesesquare/tree/scroll_aware_fab"
 *        target="_blank">Demos the new Android Design library</a>
 * @since 1.0.0
 */
public final class FabScrollAwareBehavior extends
        AppBarLayout.ScrollingViewBehavior  {

    /**
     * Constructor.
     *
     * @param ctx   The context.
     * @param attrs The attributes. May be {@code null}.
     */
    public FabScrollAwareBehavior(@NonNull Context ctx,
            @Nullable AttributeSet attrs) {
        super(ctx, attrs);
    }

    @Override
    public boolean layoutDependsOn(@Nullable CoordinatorLayout parent,
            @Nullable View child, @Nullable View dependency) {
        return (dependency instanceof FloatingActionButton) ||
                super.layoutDependsOn(parent, child, dependency);
    }

    @Override
    public boolean onStartNestedScroll(
            @Nullable CoordinatorLayout coordinatorLayout,
            @Nullable View child, @Nullable View directTargetChild,
            @Nullable View target, int axes, int type) {
        return (axes == ViewCompat.SCROLL_AXIS_VERTICAL) ||
                super.onStartNestedScroll(coordinatorLayout, child,
                        directTargetChild, target, axes, type);
    }

    @Override
    public void onNestedScroll(@Nullable CoordinatorLayout coordinatorLayout,
            @Nullable View child, @Nullable View target, int dxConsumed,
            int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        FloatingActionButton fab;
        List<View> deps;

        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed,
                dyConsumed, dxUnconsumed, dyUnconsumed, type);

        if (dyConsumed > 0) {
            deps = coordinatorLayout.getDependencies(child);
            for (View view : deps) {
                if (view instanceof FloatingActionButton) {
                    fab = (FloatingActionButton) view;
                    if (fab.getVisibility() == View.VISIBLE) {
                        fab.hide();
                    }
                }
            }
        } else if (dyConsumed < 0) {
            deps = coordinatorLayout.getDependencies(child);
            for (View view : deps) {
                if (view instanceof FloatingActionButton) {
                    fab = (FloatingActionButton) view;
                    if (fab.getVisibility() != View.VISIBLE) {
                        fab.show();
                    }
                }
            }
        }
    }
}
