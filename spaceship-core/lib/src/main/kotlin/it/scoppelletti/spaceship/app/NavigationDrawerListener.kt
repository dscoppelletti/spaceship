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

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.view.View
import it.scoppelletti.spaceship.CoreExt
import it.scoppelletti.spaceship.R

/**
 * Implementation of the `DrawerListener` interface that extends the
 * `ActionBarDrawerToggle` class in order to complete the recommended design for
 * navigation drawers.
 */
internal class NavigationDrawerListener : ActionBarDrawerToggle {
    private var learned: Boolean = false
    private val activity: Activity
    private val drawerLayout: DrawerLayout

    /**
     * @constructor              Constructor.
     * @param       activity     The activity hosting the drawer.
     * @param       drawerLayout The `DrawerLayout` widget.
     */
    constructor(activity: Activity, drawerLayout: DrawerLayout) :
            super(activity, drawerLayout,
                    R.string.it_scoppelletti_cmd_drawerOpen,
                    R.string.it_scoppelletti_cmd_drawerClose) {
        this.activity = activity
        this.drawerLayout = drawerLayout
    }

    /**
     * @constructor              Constructor.
     * @param       activity     The activity hosting the drawer.
     * @param       drawerLayout The `DrawerLayout` widget.
     * @param       toolbar      The `Toolbar` widget.
     */
    constructor(activity: Activity,
                drawerLayout: DrawerLayout,
                toolbar: Toolbar
    ) : super(activity, drawerLayout, toolbar,
            R.string.it_scoppelletti_cmd_drawerOpen,
            R.string.it_scoppelletti_cmd_drawerClose) {
        this.activity = activity
        this.drawerLayout = drawerLayout
    }

    /**
     * Should be called by the like-named method of the drawer.
     *
     * @param savedInstanceState State of the activity.
     */
    fun onPostCreate(savedInstanceState: Bundle?) {
        val prefs: SharedPreferences

        syncState()

        prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        learned = prefs.getBoolean(CoreExt.PROP_LEARNED, false)
        if (!learned && savedInstanceState == null) {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    override fun onDrawerOpened(drawerView: View) {
        val prefs: SharedPreferences
        val editor: SharedPreferences.Editor

        super.onDrawerOpened(drawerView)

        if (!learned) {
            prefs = PreferenceManager.getDefaultSharedPreferences(activity)
            editor = prefs.edit()
            editor.putBoolean(CoreExt.PROP_LEARNED, true)
            editor.apply()
            learned = true
        }
    }
}