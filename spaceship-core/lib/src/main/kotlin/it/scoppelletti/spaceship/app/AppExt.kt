/*
 * Copyright (C) 2013-2017 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 * Application model operations.
 *
 * @since 1.0.0
 */
public object AppExt {

    /**
     * Property indicating whether the user has manually opened the drawer
     * at least once.
     */
    public const val PROP_LEARNED: String = "it.scoppelletti.spaceship.1"

    /**
     * Property reporting the title of an activity as a string resource ID.
     */
    public const val PROP_TITLE: String = "it.scoppelletti.spaceship.2"

    /**
     * Tag of `ConfirmDialogFragment` fragment.
     *
     * @see it.scoppelletti.spaceship.app.ConfirmDialogFragment
     */
    public const val TAG_CONFIRMDIALOG: String = "it.scoppelletti.spaceship.1"

    /**
     * Tag of `ExceptionDialogFragment` fragment.
     *
     * @see it.scoppelletti.spaceship.app.ExceptionDialogFragment
     */
    public const val TAG_EXCEPTIONDIALOG: String = "it.scoppelletti.spaceship.2"
}

/**
 * Hides the soft keyboard.
 *
 * @receiver The activity.
 * @since    1.0.0
 */
fun Activity.hideSoftKeyboard() {
    val view: View?
    val inputMgr: InputMethodManager

    view = this.currentFocus
    if (view != null) {
        inputMgr = this.getSystemService(Context.INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMgr.hideSoftInputFromWindow(view.windowToken, 0)
    }
}