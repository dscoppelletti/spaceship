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

package it.scoppelletti.spaceship.ads

import com.squareup.moshi.JsonClass
import it.scoppelletti.spaceship.ads.consent.DebugGeography
import it.scoppelletti.spaceship.types.StringExt

/**
 * Configuration of AdMob.
 *
 * @since 1.0.0
 *
 * @property serviceUrl     Service base URL.
 * @property publisherId    Publisher ID.
 * @property appId          App ID.
 * @property unitIds        Collection of unit IDs.
 * @property debugGeography Enable or disable the debug geography mode.
 */
@JsonClass(generateAdapter = true)
public data class AdsConfig(
        public val serviceUrl: String = StringExt.EMPTY,
        public val publisherId: String = StringExt.EMPTY,
        public val appId: String = StringExt.EMPTY,
        public val unitIds: List<String> = emptyList(),
        public val debugGeography: DebugGeography = DebugGeography.DISABLED
)

/**
 * Wrapper for the configuration of AdMob.
 *
 * @since 1.0.0
 */
public class AdsConfigWrapper {

    @Volatile
    public var value = AdsConfig()
}
