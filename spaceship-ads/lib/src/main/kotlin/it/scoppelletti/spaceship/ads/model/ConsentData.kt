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

package it.scoppelletti.spaceship.ads.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import it.scoppelletti.spaceship.ads.consent.ConsentStatus

/**
 * Data regarding the consent from the user to receive perzonalized advertising.
 *
 * @since 1.0.0
 *
 * @property consentStatus                 Consent status.
 * @property adProviders                   Collection of the Ad providers.
 * @property hasNonPersonalizedPublisherId Indicates whether the publisher has
 *                                         configured any non personalized Ad
 *                                         providers.
 * @property year                          Year of the consent from the user.
 */
@JsonClass(generateAdapter = true)
public data class ConsentData(

        @Json(name = "consent_state")
        public val consentStatus: ConsentStatus = ConsentStatus.UNKNOWN,

        @Json(name = "providers")
        public val adProviders: List<AdProvider> = emptyList(),

        @Json(name = "has_any_npa_pub_id")
        public val hasNonPersonalizedPublisherId: Boolean = false,

        @Json(name = "year")
        public val year: Int
)