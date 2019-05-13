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

@file:Suppress("RedundantVisibilityModifier", "unused")

package it.scoppelletti.spaceship.ads.inject

import dagger.Binds
import dagger.Lazy
import dagger.Module
import dagger.Provides
import it.scoppelletti.spaceship.ads.AdsExt
import it.scoppelletti.spaceship.ads.AdsConfig
import it.scoppelletti.spaceship.ads.consent.AdService
import it.scoppelletti.spaceship.ads.consent.ConsentDataLoader
import it.scoppelletti.spaceship.ads.consent.ConsentDataStore
import it.scoppelletti.spaceship.ads.consent.DefaultConsentDataLoader
import it.scoppelletti.spaceship.ads.consent.DefaultConsentDataStore
import it.scoppelletti.spaceship.inject.CoreModule
import it.scoppelletti.spaceship.inject.IOModule
import it.scoppelletti.spaceship.inject.TimeModule
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named

/**
 * Defines the dependencies exported by this library.
 *
 * @since 1.0.0
 */
@Module(includes = [ CoreModule::class, IOModule::class, TimeModule::class ])
public abstract class AdsModule {

    @Binds
    public abstract fun bindConsentDataLoader(
            obj: DefaultConsentDataLoader
    ): ConsentDataLoader

    @Binds
    public abstract fun bindConsentDataStore(
            obj: DefaultConsentDataStore
    ): ConsentDataStore

    @Module
    public companion object {

        @Volatile
        private lateinit var adsConfig: AdsConfig

        /**
         * Registers the `AdsConfig` object.
         *
         * @param obj The object.
         */
        public fun registerAdsConfig(obj: AdsConfig) {
            adsConfig = obj
        }

        @Provides
        @JvmStatic
        public fun provideAdsConfig(): AdsConfig = adsConfig

        @Provides
        @JvmStatic
        @Named(AdsExt.DEP_HTTPCLIENT)
        public fun provideHttpClient(): OkHttpClient =
                OkHttpClient.Builder().build()

        @Provides
        @JvmStatic
        @Named(AdsExt.DEP_RETROFIT)
        public fun provideRetrofit(
                adsConfig: AdsConfig,
                @Named(AdsExt.DEP_HTTPCLIENT) httpClient: Lazy<OkHttpClient>
        ): Retrofit {
            return Retrofit.Builder()
                    .baseUrl(adsConfig.serviceUrl)
                    .addConverterFactory(MoshiConverterFactory.create())
                    .callFactory { req -> httpClient.get().newCall(req) }
                    .build()
        }

        @Provides
        @JvmStatic
        public fun provideAdService(
                @Named(AdsExt.DEP_RETROFIT) retrofit: Retrofit
        ): AdService = retrofit.create(AdService::class.java)
    }
}
