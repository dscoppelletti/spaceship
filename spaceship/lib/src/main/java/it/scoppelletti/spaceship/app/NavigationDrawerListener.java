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

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import it.scoppelletti.spaceship.R;

/**
 * Implementation of the {@code DrawerListener} interface that extends the
 * {@code ActionBarDrawerToggle} class in order to complete the recommended
 * design for navigation drawers.
 */
final class NavigationDrawerListener extends ActionBarDrawerToggle {
    private final Activity myActivity;
    private final DrawerLayout myLayout;
    private boolean myLearned;

    /**
     * Constructor.
     *
     * @param activity The activity hosting the drawer.
     * @param layout   The {@code DrawerLayout} widget.
     */
    NavigationDrawerListener(@NonNull Activity activity,
            @NonNull DrawerLayout layout) {
        super(activity, layout, R.string.it_scoppelletti_cmd_drawerOpen,
                R.string.it_scoppelletti_cmd_drawerClose);
        myActivity = activity;
        myLayout = layout;
    }

    /**
     * Constructor.
     *
     * @param activity The activity hosting the drawer.
     * @param layout   The {@code DrawerLayout} widget.
     * @param toolbar  The toolbar widget.
     */
    NavigationDrawerListener(@NonNull Activity activity,
            @NonNull DrawerLayout layout, @NonNull Toolbar toolbar) {
        super(activity, layout, toolbar,
                R.string.it_scoppelletti_cmd_drawerOpen,
                R.string.it_scoppelletti_cmd_drawerClose);
        myActivity = activity;
        myLayout = layout;
    }

    /**
     * Should be called by the like-named method of the drawer.
     *
     * @param savedInstanceState State of the activity. May be {@code null}.
     */
    void onPostCreate(@Nullable Bundle savedInstanceState) {
        SharedPreferences prefs;

        syncState();

        prefs = PreferenceManager.getDefaultSharedPreferences(myActivity);
        myLearned = prefs.getBoolean(AppExt.PROP_LEARNED, false);
        if (!myLearned && savedInstanceState == null) {
            myLayout.openDrawer(GravityCompat.START);
        }
    }

    @Override
    public void onDrawerOpened(View drawerView) {
        SharedPreferences prefs;
        SharedPreferences.Editor editor;

        super.onDrawerOpened(drawerView);

        if (!myLearned) {
            prefs = PreferenceManager.getDefaultSharedPreferences(myActivity);
            editor = prefs.edit();
            editor.putBoolean(AppExt.PROP_LEARNED, true);
            editor.apply();
            myLearned = true;
        }
    }
}
