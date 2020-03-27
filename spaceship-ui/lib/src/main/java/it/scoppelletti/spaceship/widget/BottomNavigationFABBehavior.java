/*
 * Copyright 2018 TonyTangAndroid.
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
 * - Dario Scoppelletti, 2019
 * Repository: http://github.com/TonyTangAndroid/GooglePlusDemo
 * File: app/src/main/java/io/anycopy/googleplusdemo/BottomNavigationFABBehavior
 * Commit: 245cf48f698d9197627553fd6720530f7b45f5db - December 19, 2018
 * Suppress warnings.
 * Add @Override annotations.
 * Add Javadoc.
 */

package it.scoppelletti.spaceship.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

/**
 * Behavior to make a {@code FloatingActionButton} supports
 * {@code BottomNavigationView}s.
 *
 * @see   <a href="http://medium.com/@tonythompsoncmu/integrate-coordinatorlayout-bottomnavigationview-toolbar-tablayout-viewpager-fragment-e4268e83b475"
 *        target="_top">Integrate CoordinatorLayout + BottomNavigationView +
 *        Toolbar + TabLayout + ViewPager + Fragment + DrawerLayout</a>
 * @see   <a href="http://medium.com/@rusinikita/simple-view-dodging-with-coordinatorlayout-f13cc32e0de6"
 *        target="_top">Simple view dodging with CoordinatorLayout</a>
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public final class BottomNavigationFABBehavior extends
        CoordinatorLayout.Behavior<FloatingActionButton> {

    /**
     * Constructor.
     *
     * @param context Context.
     * @param attrs   Attributes.
     */
    public BottomNavigationFABBehavior(@Nullable Context context,
            @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(@Nullable CoordinatorLayout parent,
            @NonNull FloatingActionButton child, @NonNull View dependency) {
        return dependency instanceof Snackbar.SnackbarLayout;
    }

    @Override
    public void onDependentViewRemoved(@NonNull CoordinatorLayout parent,
            @NonNull FloatingActionButton child, @NonNull View dependency) {
        child.setTranslationY(0.0f);
    }

    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent,
            @NonNull FloatingActionButton child, @NonNull View dependency) {
        return this.updateButton(child, dependency);
    }

    private boolean updateButton(View child, View dependency) {
        if (dependency instanceof Snackbar.SnackbarLayout) {
            float oldTranslation = child.getTranslationY();
            float height = (float) dependency.getHeight();
            float newTranslation = dependency.getTranslationY() - height;
            child.setTranslationY(newTranslation);
            return oldTranslation != newTranslation;
        } else {
            return false;
        }
    }
}
