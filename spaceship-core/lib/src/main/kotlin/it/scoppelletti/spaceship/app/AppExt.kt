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

@file:Suppress("JoinDeclarationAndAssignment", "RedundantVisibilityModifier")

package it.scoppelletti.spaceship.app

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Status
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import java.lang.Exception

/**
 * Tries to finish an activity.
 *
 * @receiver Activity.
 * @return   Returns `true` if the finish process has been started, `false` if
 *           this activity was already finishing.
 * @since    1.0.0
 */
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

/**
 * Attempts to make Google Play services available on this device.
 *
 * @receiver Activity
 * @return   The new task.
 * @since    1.0.0
 */
public fun Activity.makeGooglePlayServicesAvailable() : Task<Void> {
    val result: Int
    val googleApi: GoogleApiAvailability
    val ex: Exception

    googleApi = GoogleApiAvailability.getInstance()
    result = googleApi.isGooglePlayServicesAvailable(this)
    if (result == ConnectionResult.SERVICE_INVALID) {
        // http://issuetracker.google.com/issues/120871359 - Dec 12, 2018
        ex = ApiException(Status(result))
        return Tasks.forException(ex)
    }

    return googleApi.makeGooglePlayServicesAvailable(this)
}
