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

@file:Suppress("RedundantVisibilityModifier")

package it.scoppelletti.spaceship.ads.consent

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Status of the consent from the user to receive perzonalized advertising.
 *
 * @since 1.0.0
 */
@JsonClass(generateAdapter = false)
public enum class ConsentStatus {

    /**
     * The user has to be prompted for the consent.
     */
    @Json(name = "unknown")
    UNKNOWN,

    /**
     * The user is not located in the European Economic Area.
     */
    @Json(name = "request_not_in_eea")
    NOT_IN_EEA,

    /**
     * The user is under age of consent.
     *
     * * [Users under the age of consent](http://developers.google.com/admob/android/targeting#users_under_the_age_of_consent)
     */
    @Json(name = "under_age_of_consent")
    UNDER_AGE_OF_CONSENT,

    /**
     * The user has not consented to receive personalized advertising.
     */
    @Json(name = "non_personalized")
    NON_PERSONALIZED,

    /**
     * The user has consented to receive personalized advertising.
     */
    @Json(name = "personalized")
    PERSONALIZED
}
