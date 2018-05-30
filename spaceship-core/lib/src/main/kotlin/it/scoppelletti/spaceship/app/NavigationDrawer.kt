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

package it.scoppelletti.spaceship.app

import android.content.res.Configuration
import android.os.Bundle
import android.support.annotation.MainThread
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem

/**
 * Navigation Drawer.
 *
 * * [Navigation with Back and Up](http://developer.android.com/design/patterns/navigation.html)
 * * [Creating a Navigation Drawer](http://developer.android.com/training/implementing-navigation/nav-drawer.html)
 * * [Layout > Structure](http://material.io/guidelines/layout/structure.html)
 * * [Patterns > Navigation drawer](http://material.io/guidelines/patterns/navigation-drawer.html)
 *
 * @since 1.0.0
 *
 * @constructor                Constructor.
 * @param       activity       The activity hosting the drawer.
 * @param       drawerLayout   The `DrawerLayout` widget.
 * @param       navigationView The `NavigationView` widget.
 * @param       toolbar        The `Toolbar` widget.
 */
@MainThread
public class NavigationDrawer(
        private val activity: AppCompatActivity,
        private val drawerLayout: DrawerLayout,
        private val navigationView: NavigationView,
        private val toolbar: Toolbar?) {
    private lateinit var toggle: NavigationDrawerListener

    /**
     * Should be called by the like-named method of the activity.
     *
     * @param savedInstanceState State of the activity.
     */
    public fun onCreate(
            @Suppress("UNUSED_PARAMETER") savedInstanceState: Bundle?
    ) {
        toggle = when(toolbar) {
            null -> NavigationDrawerListener(activity, drawerLayout)
            else -> NavigationDrawerListener(activity, drawerLayout, toolbar)
        }

        drawerLayout.addDrawerListener(toggle)
    }

    /**
     * Should be called by the like-named method of the activity.
     *
     * @param savedInstanceState State of the activity.
     */
    public fun onPostCreate(savedInstanceState: Bundle?) {
        toggle.onPostCreate(savedInstanceState)
    }

    /**
     * Should be called by the like-named method of the activity.
     *
     * If it returns `true`, the calling method should skip further processing.
     *
     * @return Returns `true` if the event has been handled, `false` otherwise.
     */
    public fun onBackPressed(): Boolean {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
            return true
        }

        return false
    }

    /**
     * Should be called by the like-named method of the activity.
     *
     * @param newConfig The new device configuration.
     */
    public fun onConfigurationChanged(newConfig: Configuration?) =
            toggle.onConfigurationChanged(newConfig)

    /**
     * Should be called by the like-named method of the activity.
     *
     * If it returns `true`, the calling method should skip further processing
     * and return `true`.
     *
     * @return Returns `true` if the event has been handled, `false` otherwise.
     */
    public fun onOptionItemSelected(item: MenuItem?) =
            toggle.onOptionsItemSelected(item)
}