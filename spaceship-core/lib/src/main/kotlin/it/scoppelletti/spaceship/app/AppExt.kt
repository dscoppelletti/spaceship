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