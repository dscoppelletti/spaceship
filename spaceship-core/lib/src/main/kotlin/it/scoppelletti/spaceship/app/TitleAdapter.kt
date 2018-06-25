/*
 * Copyright (C) 2016 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v7.app.AppCompatActivity
import it.scoppelletti.spaceship.CoreExt

/**
 * Title adapter.
 *
 * A `TitleAdapter` object lets you set the title of an activity by the only
 * one property `title` even if you set a custom `Toolbar` as the action bar or
 * if you use a `CollapsingToolbarLayout` widget.
 *
 * @since            1.0.0
 * @property titleId The title as a string resource ID.
 *
 * @constructor               Constructor.
 * @param       activity      The activity.
 * @param       toolbarLayout The `CollapsingToolbarLayout` widget.
 */
public class TitleAdapter(
        private val activity: AppCompatActivity,
        private val toolbarLayout: CollapsingToolbarLayout? = null
) {
    @StringRes public var titleId: Int = -1
        set(value) {
            if (toolbarLayout == null) {
                activity.supportActionBar?.setTitle(value)
            } else {
                toolbarLayout.title = activity.getString(value)
            }

            activity.setTitle(value)
            field = value
        }

    /**
     * Should be called by the like-named method of the activity.
     *
     * @param savedInstanceState State of the activity.
     */
    public fun onPostCreate(savedInstanceState: Bundle?) {
        val t = savedInstanceState?.getInt(CoreExt.PROP_TITLE, -1) ?: -1
        if (t > 0) {
            titleId = t
        }
    }

    /**
     * Should be called by the like-named method of the activity.
     *
     * @param outState State of the activity.
     */
    public fun onSaveInstanceState(outState: Bundle?) {
        if (titleId > 0) {
            outState?.putInt(CoreExt.PROP_TITLE, titleId)
        }
    }
}