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
import it.scoppelletti.spaceship.ApplicationException
import it.scoppelletti.spaceship.gms.coroutines.suspendTask
import it.scoppelletti.spaceship.gms.i18n.GmsMessages
import it.scoppelletti.spaceship.gms.inject.GmsComponent
import it.scoppelletti.spaceship.gms.inject.GmsComponentProvider
import java.lang.Exception
import java.util.concurrent.CancellationException

/**
 * Returns the `GmsComponent` component.
 *
 * @receiver Activity.
 * @return   The object.
 * @since    1.0.0
 */
public fun Activity.gmsComponent(): GmsComponent =
        (this.application as GmsComponentProvider).gmsComponent()

/**
 * Attempts to make Google Play services available on this device.
 *
 * @param activity Activity
 * @since          1.0.0
 */
@UiThread
public suspend fun makeGooglePlayServicesAvailable(activity: Activity) {
    val result: Int
    val googleApi: GoogleApiAvailability
    val gmsMessages: GmsMessages

    gmsMessages = activity.gmsComponent().gmsMessages()
    try {
        googleApi = GoogleApiAvailability.getInstance()
        result = googleApi.isGooglePlayServicesAvailable(activity)
        if (result == ConnectionResult.SERVICE_INVALID) {
            // http://issuetracker.google.com/issues/120871359 - Dec 12, 2018
            throw ApiException(Status(result))
        }

        suspendTask(googleApi::makeGooglePlayServicesAvailable, activity)
    } catch (ex: CancellationException) {
        throw ex
    } catch (ex: Exception) {
        throw ApplicationException(gmsMessages.errorGoogleApiNotAvailable(), ex)
    }
}

