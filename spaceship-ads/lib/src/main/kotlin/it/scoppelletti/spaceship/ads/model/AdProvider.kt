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
 * Ad provider info.
 *
 * @since 1.0.0
 *
 * @property companyId Provider ID.
 * @property name      Name of the provider.
 * @property policyUrl URL of the privacy policy.
 */
@JsonClass(generateAdapter = true)
public data class AdProvider(

        @Json(name = "company_id")
        public val companyId: String,

        @Json(name = "company_name")
        public val name: String,

        @Json(name = "policy_url")
        public val policyUrl: String
)
