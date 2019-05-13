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

/**
 * AbMob server configuration.
 *
 * @see   it.scoppelletti.spaceship.ads.consent.AdService
 * @since 1.0.0
 *
 * @property companies                       Collection of the Ad providers.
 * @property adNetworkLookupResponses        Collection of configurations of the
 *                                           requested publisher.
 * @property isRequestLocationInEeaOrUnknown Indicates whether the user is
 *                                           located in the European Economic
 *                                           Area.
 */
@JsonClass(generateAdapter = true)
public class ServerResponse(

        @Json(name = "companies")
        public val companies: List<AdProvider>,

        @Json(name = "ad_network_ids")
        public val adNetworkLookupResponses: List<AdNetworkLookupResponse>,

        @Json(name = "is_request_in_eea_or_unknown")
        public val isRequestLocationInEeaOrUnknown: Boolean
)

