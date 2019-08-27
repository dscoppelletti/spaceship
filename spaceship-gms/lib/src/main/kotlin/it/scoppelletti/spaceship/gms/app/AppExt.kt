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

package it.scoppelletti.spaceship.gms.app

import android.app.Activity
import androidx.annotation.UiThread
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Status
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks

/**
 * Attempts to make Google Play services available on this device.
 *
 * @receiver Activity
 * @return   The new task.
 * @since    1.0.0
 */
@UiThread
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
