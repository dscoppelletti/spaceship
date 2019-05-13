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

import it.scoppelletti.spaceship.ads.AdsExt
import it.scoppelletti.spaceship.ads.model.ServerResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Client interface to Ad Service.
 *
 * @since 1.0.0
 */
public interface AdService {

    /**
     * Gets the AdMob server configuration.
     *
     * @param  publisherId    Publisher ID.
     * @param  debugGeography Enable or disable the debug geography mode.
     * @param  es             n.u.
     * @param  platform       Client platform.
     * @param  version        API version.
     * @return                The new observable.
     */
    @GET("getconfig/pubvendors")
    suspend fun getConfig(
            @Query("pubs") publisherId: String,
            @Query("debug_geo") debugGeography: Int =
                    DebugGeography.DISABLED.code,
            @Query("es") es: String = "2",
            @Query("plat") platform: String = AdsExt.API_PLATFORM,
            @Query("v") version: String = AdsExt.API_VERSION
    ) : ServerResponse
}