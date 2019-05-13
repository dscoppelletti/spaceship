/*
 * Copyright (C) 2018 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

package it.scoppelletti.spaceship.ads

import android.os.Bundle
import androidx.annotation.StringRes
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdRequest
import it.scoppelletti.spaceship.ads.consent.ConsentStatus

/**
 * Operations for Google Ads.
 *
 * @since 1.0.0
 */
public object AdsExt {

    /**
     * Client platform.
     */
    public const val API_PLATFORM = "android"

    /**
     * Consent API version.
     */
    public const val API_VERSION = "1.0.7"

    /**
     * Name of the `HttpClient` dependency.
     */
    public const val DEP_HTTPCLIENT = "it.scoppelletti.spaceship.ads.1"

    /**
     * Name of the `Retrofit` dependency.
     */
    public const val DEP_RETROFIT = "it.scoppelletti.spaceship.ads.2"

    /**
     * Property indicating whether an activity has been launched as a settings
     * activity.
     */
    public const val PROP_SETTINGS = "it.scoppelletti.spaceship.ads.2"

    /**
     * Property indicating that the user has not consented to receive
     * personalized advertising.
     *
     * @see it.scoppelletti.spaceship.ads.AdsExt.NPA_TRUE
     */
    public const val PROP_NPA = "npa"

    /**
     * Value indicating that the user has not consented to receive personalized
     * advertising.
     *
     * @see it.scoppelletti.spaceship.ads.AdsExt.PROP_NPA
     */
    public const val NPA_TRUE = "1"

    /**
     * Property indicating that the User is Under the Age of Consent in Europe.
     *
     * * [Users under the age of consent](http://developers.google.com/admob/android/targeting#users_under_the_age_of_consent)
     */
    public const val PROP_TFUA = "tag_for_under_age_of_consent"
}

/**
 * Returns the message corresponding to an error code.
 *
 * @param  errorCode The error code.
 * @return           The message as a string resource ID.
 * @since            1.0.0
 */
@StringRes
@Suppress("unused")
public fun adsErrorCodeToMessageId(errorCode: Int): Int =
        when (errorCode) {
            AdRequest.ERROR_CODE_INTERNAL_ERROR ->
                R.string.it_scoppelletti_ads_err_internal
            AdRequest.ERROR_CODE_INVALID_REQUEST ->
                R.string.it_scoppelletti_ads_err_invalid_request
            AdRequest.ERROR_CODE_NETWORK_ERROR ->
                R.string.it_scoppelletti_ads_err_network
            AdRequest.ERROR_CODE_NO_FILL ->
                R.string.it_scoppelletti_ads_err_no_fill
            else -> R.string.it_scoppelletti_ads_err_unknown
        }

/**
 * Creates a new `AdRequest.Builder` instance.
 *
 * @param  consentStatus Consent status.
 * @return               The new object.
 * @since                1.0.0
 */
public fun adRequestBuilder(consentStatus: ConsentStatus): AdRequest.Builder {
    val adBuilder: AdRequest.Builder
    val extra: Bundle

    adBuilder = AdRequest.Builder()

    when (consentStatus) {
        ConsentStatus.NON_PERSONALIZED -> {
            extra = Bundle()
            extra.putString(AdsExt.PROP_NPA, AdsExt.NPA_TRUE)
            adBuilder.addNetworkExtrasBundle(AdMobAdapter::class.java,
                    extra)
        }

        ConsentStatus.UNDER_AGE_OF_CONSENT -> {
            extra = Bundle()
            extra.putBoolean(AdsExt.PROP_TFUA, true)
            adBuilder.addNetworkExtrasBundle(AdMobAdapter::class.java,
                    extra)
        }

        else -> {
            // ConsentStatus.PERSONALIZED, ConsentStatus.NOT_IN_EEA
        }
    }

    return adBuilder
}
