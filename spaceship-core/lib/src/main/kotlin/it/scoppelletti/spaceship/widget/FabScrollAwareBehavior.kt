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

package it.scoppelletti.spaceship.widget

import android.content.Context
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.View

/**
 * Behaviour for attaching a FAB to a vertical-scrolling widget.
 *
 * * [Floating Action Buttons > Animating the Floating Action Button](http://github.com/codepath/android_guides/wiki/Floating-Action-Buttons)
 * * [Demos the new Android Design library](http://github.com/ianhanniballake/cheesesquare/tree/scroll_aware_fab)
 *
 * @since 1.0.0
 *
 * @constructor       Constructor.
 * @param       ctx   The context.
 * @param       attrs The attributes.
 */
public class FabScrollAwareBehavior(
        ctx: Context,
        attrs: AttributeSet?
) : AppBarLayout.ScrollingViewBehavior(ctx, attrs) {
    // - Genymotion 2.11.0
    // FAB never hides likely because "pixel perfect" is disabled.

    override fun layoutDependsOn(
            parent: CoordinatorLayout?,
            child: View?,
            dependency: View?
    ): Boolean = dependency is FloatingActionButton ||
                super.layoutDependsOn(parent, child, dependency)

    override fun onStartNestedScroll(
            coordinatorLayout: CoordinatorLayout,
            child: View,
            directTargetChild: View,
            target: View,
            axes: Int,
            type: Int
    ): Boolean = axes == ViewCompat.SCROLL_AXIS_VERTICAL ||
                super.onStartNestedScroll(coordinatorLayout, child,
                        directTargetChild, target, axes, type)

    override fun onNestedScroll(
            coordinatorLayout: CoordinatorLayout,
            child: View,
            target: View,
            dxConsumed: Int,
            dyConsumed: Int,
            dxUnconsumed: Int,
            dyUnconsumed: Int,
            type: Int
    ) {
        val deps: List<View>

        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed,
                dyConsumed, dxUnconsumed, dyUnconsumed, type)

        if (dyConsumed > 0) {
            deps = coordinatorLayout.getDependencies(child)
            for (view in deps) {
                if (view is FloatingActionButton &&
                                view.visibility == View.VISIBLE) {
                    view.hide()
                }
            }
        } else if (dyConsumed < 0) {
            deps = coordinatorLayout.getDependencies(child)
            for (view in deps) {
                if (view is FloatingActionButton &&
                        view.visibility != View.VISIBLE) {
                    view.show()
                }
            }
        }
    }
}
