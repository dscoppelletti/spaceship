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

@file:Suppress("JoinDeclarationAndAssignment", "RedundantVisibilityModifier",
        "unused")

package it.scoppelletti.spaceship.app

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.UiThread
import it.scoppelletti.spaceship.inject.UIComponent
import it.scoppelletti.spaceship.inject.UIComponentProvider
import it.scoppelletti.spaceship.inject.StdlibComponent
import it.scoppelletti.spaceship.inject.StdlibComponentProvider

/**
 * Application model extensions.
 *
 * @since 1.0.0
 */
public object AppExt {

    /**
     * Tag of `AlertDialogFragment` fragment.
     *
     * @see it.scoppelletti.spaceship.app.AlertDialogFragment
     */
    public const val TAG_ALERTDIALOG = "it.scoppelletti.spaceship.1"

    /**
     * Tag of `ExceptionDialogFragment` fragment.
     *
     * @see it.scoppelletti.spaceship.app.ExceptionDialogFragment
     */
    public const val TAG_EXCEPTIONDIALOG = "it.scoppelletti.spaceship.2"

    /**
     * Tag of `BottomSheetDialogFragmentEx` fragment.
     *
     * @see it.scoppelletti.spaceship.app.BottomSheetDialogFragmentEx
     */
    public const val TAG_BOTTOMSHEETDIALOG = "it.scoppelletti.spaceship.3"

    /**
     * Tag of `DateDialogFragment` fragment.
     *
     * @see it.scoppelletti.spaceship.app.DateDialogFragment
     */
    public const val TAG_DATEDIALOG = "it.scoppelletti.spaceship.4"

    /**
     * Tag of `TimeDialogFragment` fragment.
     *
     * @see it.scoppelletti.spaceship.app.TimeDialogFragment
     */
    public const val TAG_TIMEDIALOG = "it.scoppelletti.spaceship.5"
}

/**
 * Returns the `UIComponent` component.
 *
 * @receiver Activity.
 * @return   The object.
 * @since    1.0.0
 */
public fun Activity.uiComponent(): UIComponent =
        (this.application as UIComponentProvider).uiComponent()

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
    val view: View?
    val inputMgr: InputMethodManager

    view = this.currentFocus
    if (view != null) {
        inputMgr = this.getSystemService(Context.INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMgr.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
