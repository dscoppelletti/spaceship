/*
 * Copyright (C) 2013-2021 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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
import android.view.inputmethod.InputMethodManager
import androidx.annotation.UiThread
import it.scoppelletti.spaceship.inject.AppComponent
import it.scoppelletti.spaceship.inject.AppComponentProvider
import it.scoppelletti.spaceship.inject.StdlibComponent
import it.scoppelletti.spaceship.inject.StdlibComponentProvider

/**
 * Returns the `AppComponent` component.
 *
 * @receiver Activity.
 * @return   The object.
 * @since    1.0.0
 */
public fun Activity.appComponent(): AppComponent =
    (this.application as AppComponentProvider).appComponent()

/**
 * Returns the `StdlibComponent` component.
 *
 * @receiver Activity.
 * @return   The object.
 * @since    1.0.0
 */
public fun Activity.stdlibComponent(): StdlibComponent =
    (this.application as StdlibComponentProvider).stdlibComponent()

/**
 * Tries to finish an activity.
 *
 * @receiver Activity.
 * @return   Returns `true` if the finish process has been started, `false` if
 *           this activity was already finishing.
 * @since    1.0.0
 */
@UiThread
public fun Activity.tryFinish(): Boolean {
    if (this.isFinishing) {
        return false
    }

    this.finish()
    return true
}

/**
 * Hides the soft keyboard.
 *
 * @receiver Activity.
 * @since    1.0.0
 */
@UiThread
public fun Activity.hideSoftKeyboard() {
    val inputMgr: InputMethodManager

    val view = this.currentFocus
    if (view != null) {
        inputMgr = this.getSystemService(Context.INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMgr.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

