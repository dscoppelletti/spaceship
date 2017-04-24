/*
 * Copyright (C) 2014-2015 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

package it.scoppelletti.spaceship.app;

import java.util.Locale;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

/**
 * Navigation Drawer.
 *
 * @see   <a href="http://developer.android.com/design/patterns/navigation.html"
 *        target="_top">Navigation with Back and Up</a>
 * @see   <a href="http://developer.android.com/training/implementing-navigation/nav-drawer.html"
 *        target="_top">Creating a Navigation Drawer</a>
 * @see   <a href="http://www.google.com/design/spec/layout/structure.html"
 *        target="_top">Layout &gt; Structure</a>
 * @see   <a href="http://www.google.com/design/spec/patterns/navigation-drawer.html"
 *        target="_top">Patterns &gt; Navigation drawer</a>
 * @since 1.0.0
 */
@MainThread
public final class NavigationDrawer {
    private final AppCompatActivity myActivity;
    private final DrawerLayout myLayout;
    private final NavigationView myView;
    private final Toolbar myToolbar;
    private final NavigationView.OnNavigationItemSelectedListener
            myOnItemSelectedListener;
    private NavigationDrawerListener myToggle;

    /**
     * Constructor.
     *
     * @param builder The instance builder.
     */
    private NavigationDrawer(NavigationDrawer.Builder builder) {
        myActivity = (AppCompatActivity) builder.myActivity;

        myLayout = (DrawerLayout) myActivity.findViewById(builder.myLayoutId);
        if (myLayout == null) {
            throw new NullPointerException(String.format(Locale.ENGLISH,
                    "No DrawerLayout widget with identifier %1$d.",
                    builder.myLayoutId));
        }

        myView = (NavigationView) myActivity.findViewById(builder.myViewId);
        if (myView == null) {
            throw new NullPointerException(String.format(Locale.ENGLISH,
                    "No NavigationView widget with identifier %1$d.",
                    builder.myLayoutId));
        }

        if (builder.myToolbarId != View.NO_ID) {
            myToolbar = (Toolbar) myActivity.findViewById(builder.myToolbarId);
            if (myToolbar == null) {
                throw new NullPointerException(String.format(Locale.ENGLISH,
                        "No Toolbar widget with identifier %1$d.",
                        builder.myLayoutId));
            }
        } else {
            myToolbar = null;
        }

        myOnItemSelectedListener = builder.myOnItemSelectedListener;
    }

    /**
     * Should be called by the namesake method of the activity.
     *
     * @param savedInstanceState The state of the activity. May be {@code null}.
     * @see   android.app.Activity#onCreate(android.os.Bundle)
     */
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (myToolbar == null) {
            myToggle = new NavigationDrawerListener(myActivity, myLayout);
        } else {
            myToggle = new NavigationDrawerListener(myActivity, myLayout,
                    myToolbar);
        }

        myLayout.addDrawerListener(myToggle);

        if (myOnItemSelectedListener != null) {
            myView.setNavigationItemSelectedListener(myOnItemSelectedListener);
        }
    }

    /**
     * Gets the {@code NavigationView} widget.
     *
     * @return The object.
     */
    @NonNull
    public NavigationView getNavigationView() {
        return myView;
    }

    /**
     * Gets the {@code Toolbar} widget.
     *
     * @return The object. May be {@code null}.
     */
    @Nullable
    public Toolbar getToolbar() {
        return myToolbar;
    }

    /**
     * Should be called by the namesake method of the activity.
     *
     * @param savedInstanceState State of the activity. May be {@code null}.
     * @see   android.app.Activity#onPostCreate(android.os.Bundle)
     */
    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        if (myToggle != null) {
            myToggle.onPostCreate(savedInstanceState);
        }
    }

    /**
     * Should be called by the namesake method of the activity. If it returns
     * {@code true}, the calling method should return {@code true} and skip
     * further processing.
     *
     * @return Returns {@code true} if the event has been handled, {@code false}
     *         otherwise.
     * @see    android.app.Activity#onBackPressed()
     */
    public boolean onBackPressed() {
        if (myLayout.isDrawerOpen(GravityCompat.START)) {
            myLayout.closeDrawer(GravityCompat.START);
            return true;
        }

        return false;
    }

    /**
     * Should be called by the namesake method of the activity.
     *
     * @param newConfig The new device configuration.
     * @see   android.app.Activity#onConfigurationChanged(android.content.res.Configuration)
     */
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        if (myToggle != null) {
            myToggle.onConfigurationChanged(newConfig);
        }
    }

    /**
     * Should be called by the namesake method of the activity. If it returns
     * {@code true}, the calling method should return {@code true} and skip
     * further processing.
     *
     * @param  item The menu item.
     * @return      Returns {@code true} if the event has been handled,
     *              {@code false} otherwise.
     * @see    android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    public boolean onOptionItemSelected(@NonNull MenuItem item) {
        if (myToggle != null && myToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return false;
    }

    /**
     * Closes the drawer.
     */
    public void closeDrawer() {
        myLayout.closeDrawers();
    }

    /**
     * Sets the currently checked item.
     *
     * @param id ID of the item to check.
     */
    public void setCheckedItem(@IdRes int id) {
        myView.setCheckedItem(id);
    }

    /**
     * Builds a {@code NavigationDrawer} instance.
     *
     * @since 1.0.0
     */
    public static final class Builder {
        private final Activity myActivity;
        private int myLayoutId;
        private int myViewId;
        private int myToolbarId;
        private NavigationView.OnNavigationItemSelectedListener
                myOnItemSelectedListener;

        /**
         * Constructor.
         *
         * @param activity The activity hosting the drawer.
         */
        public Builder(@NonNull Activity activity) {
            if (activity == null) {
                throw new NullPointerException("Argument activity is null.");
            }

            myActivity = activity;
            myToolbarId = View.NO_ID;
        }

        /**
         * Sets the ID of the {@code DrawerLayout} widget.
         *
         * @param  value The value.
         * @return       This object.
         * @see          android.support.v4.widget.DrawerLayout
         */
        @NonNull
        public NavigationDrawer.Builder layoutId(@IdRes int value) {
            myLayoutId = value;
            return this;
        }

        /**
         * Sets the ID of the {@code NavigationView} widget.
         *
         * @param  value The value.
         * @return       This object.
         * @see          android.support.design.widget.NavigationView
         */
        @NonNull
        public NavigationDrawer.Builder viewId(@IdRes int value) {
            myViewId = value;
            return this;
        }

        /**
         * Sets the ID of the {@code Toolbar} widget.
         *
         * @param  value The value.
         * @return       This object.
         * @see          android.support.v7.widget.Toolbar
         */
        @NonNull
        public NavigationDrawer.Builder toolbarId(@IdRes int value) {
            myToolbarId = value;
            return this;
        }

        /**
         * Sets the listener that will be notified when an item in the
         * navigation menu is selected.
         *
         * @param  obj The object. May be {@code null}.
         * @return     This object.
         */
        @NonNull
        public NavigationDrawer.Builder onItemSelectedIListener(
                @Nullable NavigationView.OnNavigationItemSelectedListener obj) {
            myOnItemSelectedListener = obj;
            return this;
        }

        /**
         * Builds a new {@code NavigationDrawer} instance.
         *
         * @return The new object.
         */
        @NonNull
        public NavigationDrawer build() {
            return new NavigationDrawer(this);
        }
    }
}
