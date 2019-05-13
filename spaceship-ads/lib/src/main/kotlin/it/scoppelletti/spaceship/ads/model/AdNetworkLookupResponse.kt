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
 * Configuration for a publisher.
 *
 * @since 1.0.0
 *
 * @property networkId    Publisher ID.
 * @property companyIds   Collection of the non personalized Ad providers.
 * @property lookupFailed Indicates any network error.
 * @property notFound     Indicates that the requested publisher is not found.
 * @property isNPA        Indicates whether the publisher has configured any non
 *                        personalized Ad providers.
 */
@JsonClass(generateAdapter = true)
public class AdNetworkLookupResponse(

        @Json(name = "ad_network_id")
        public val networkId: String,

        @Json(name = "company_ids")
        public val companyIds: List<String>,

        @Json(name = "lookup_failed")
        public val lookupFailed: Boolean = false,

        @Json(name = "not_found")
        public val notFound: Boolean = false,

        @Json(name = "is_npa")
        public val isNPA: Boolean = false
)
