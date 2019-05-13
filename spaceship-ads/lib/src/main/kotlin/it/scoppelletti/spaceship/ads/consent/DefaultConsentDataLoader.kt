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

package it.scoppelletti.spaceship.ads.consent

import it.scoppelletti.spaceship.ads.AdsConfig
import it.scoppelletti.spaceship.ads.R
import it.scoppelletti.spaceship.ads.model.AdNetworkLookupResponse
import it.scoppelletti.spaceship.ads.model.AdProvider
import it.scoppelletti.spaceship.ads.model.ConsentData
import it.scoppelletti.spaceship.ads.model.ServerResponse
import it.scoppelletti.spaceship.applicationException
import it.scoppelletti.spaceship.http.toHttpApplicationException
import it.scoppelletti.spaceship.types.TimeProvider
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.lang.Exception
import java.util.Calendar
import javax.inject.Inject

/**
 * Default implementation of the `ConsentDataLoader` interface.
 *
 * @since 1.0.0
 *
 * @constructor                  Constructor.
 * @param       timeProvider     Provides components for operations on dates and
 *                               times.
 * @param       consentDataStore Local store for the `ConsentData` object.
 * @param       adService        Client interface to Ad Service.
 * @param       adsConfig        Configuration of AdMob.
 */
public class DefaultConsentDataLoader @Inject constructor(
        timeProvider: TimeProvider,
        private val consentDataStore: ConsentDataStore,
        private val adService: AdService,
        private val adsConfig: AdsConfig
) : ConsentDataLoader {
    private val currentYear: Int

    init {
        currentYear = timeProvider.currentTime().get(Calendar.YEAR)
    }

    public override suspend fun load(): ConsentData =
            withContext(Dispatchers.Default) {
                coroutineScope {
                    val consentData: Deferred<ConsentData>
                    val serverConfig: Deferred<ConsentData>

                    consentData = async {
                        consentDataStore.load()
                    }

                    serverConfig = async {
                        getServerConfig()
                    }

                    merge(consentData.await(), serverConfig.await())
                }
            }

    /**
     * Compares the last locally stored configuration to the server
     * configuration in order to establish whether the user must to be prompted
     * for the consent.
     *
     * @param  consentData  Last locally stored configuration.
     * @param  serverConfig Server configuration.
     * @return              The resulting configuration.
     */
    private fun merge(
            consentData: ConsentData,
            serverConfig: ConsentData
    ): ConsentData {
        if (serverConfig.consentStatus == ConsentStatus.NOT_IN_EEA) {
            return serverConfig
        }

        // The user could be in EEA
        if (serverConfig.adProviders.toTypedArray() contentEquals
                consentData.adProviders.toTypedArray() &&
                serverConfig.hasNonPersonalizedPublisherId ==
                consentData.hasNonPersonalizedPublisherId &&
                (consentData.consentStatus !=
                        ConsentStatus.UNDER_AGE_OF_CONSENT ||
                        consentData.year == serverConfig.year)) {
            // Last locally stored configuration is still valid
            return consentData
        }

        // New configuration: prompt the user.
        // The user may have claimed to be under age of consent, but I prompt
        // him anyway.
        return serverConfig
    }

    /**
     * Gets the AdMob server configuration.
     *
     * @return The configuration
     */
    private suspend fun getServerConfig(): ConsentData {
        val serverConfig: ServerResponse

        serverConfig = withContext(Dispatchers.IO) {
            try {
                adService.getConfig(adsConfig.publisherId,
                        adsConfig.debugGeography.code)
            } catch (ex: Exception) {
                throw applicationException {
                    message(R.string.it_scoppelletti_ads_err_user)
                    cause = (ex as? HttpException)
                            ?.toHttpApplicationException() ?: ex
                }
            }
        }

        coroutineScope {
            val failedIds: Deferred<List<String>>
            val notFoundIds: Deferred<List<String>>

            failedIds = async {
                collectLookupFailedIds(serverConfig)
            }

            notFoundIds = async {
                collectNotFoundIds(serverConfig)
            }

            validatePublisherIds(failedIds.await(), notFoundIds.await())
        }

        return collectNPAProviders(serverConfig,
                collectNPAPublishers(serverConfig))
    }

    /**
     * Collects the ID of the publishers with any network error.
     *
     * @param  serverConfig Server configuration.
     * @return              The collection.
     */
    private fun collectLookupFailedIds(
            serverConfig: ServerResponse
    ): List<String> {
        if (!serverConfig.isRequestLocationInEeaOrUnknown) {
            return emptyList()
        }

        return serverConfig.adNetworkLookupResponses
                .filter { it.lookupFailed }
                .map { it.networkId }
    }

    /**
     * Collects the ID of the not found publishers.
     *
     * @param  serverConfig Server configuration.
     * @return              The collection.
     */
    private fun collectNotFoundIds(
            serverConfig: ServerResponse
    ): List<String> {
        if (!serverConfig.isRequestLocationInEeaOrUnknown) {
            return emptyList()
        }

        return serverConfig.adNetworkLookupResponses
                .filter { it.notFound }
                .map { it.networkId }
    }

    /**
     * Validates the publishers.
     *
     * @param  lookupFailedIds Publishers with any network error.
     * @param  notFoundIds     Not found publishers.
     */
    private fun validatePublisherIds(
            lookupFailedIds: List<String>,
            notFoundIds: List<String>
    ) {
        if (lookupFailedIds.isNotEmpty() && notFoundIds.isNotEmpty()) {
            throw applicationException {
                message(R.string.it_scoppelletti_ads_err_publisher) {
                    arguments {
                        add(lookupFailedIds.toTypedArray().contentToString())
                        add(notFoundIds.toTypedArray().contentToString())
                    }
                }
            }
        }

        if (lookupFailedIds.isNotEmpty()) {
            throw applicationException {
                message(R.string.it_scoppelletti_ads_err_lookupFailed) {
                    arguments {
                        add(lookupFailedIds.toTypedArray().contentToString())
                    }
                }
            }
        }

        if (notFoundIds.isNotEmpty())      {
            throw applicationException {
                message(R.string.it_scoppelletti_ads_err_notFound) {
                    arguments {
                        add(notFoundIds.toTypedArray().contentToString())
                    }
                }
            }
        }
    }

    /**
     * Collects the publishers with non personalized Ad providers.
     *
     * @param  serverConfig Server configuration.
     * @return              The collection.
     */
    private fun collectNPAPublishers(
            serverConfig: ServerResponse
    ): List<AdNetworkLookupResponse> = serverConfig.adNetworkLookupResponses
            .filter { it.isNPA }

    /**
     * Collects the non personalized Ad providers.
     *
     * @param  serverConfig  Server configuration.
     * @param  npaPublishers Publishers with non personalized Ad providers.
     * @return               The data.
     */
    private fun collectNPAProviders(
            serverConfig: ServerResponse,
            npaPublishers: List<AdNetworkLookupResponse>
    ): ConsentData {
        val consentStatus: ConsentStatus
        val companies: List<AdProvider>
        val companyIds: Set<String>

        consentStatus = if (serverConfig.isRequestLocationInEeaOrUnknown)
            ConsentStatus.UNKNOWN else ConsentStatus.NOT_IN_EEA

        if (npaPublishers.isEmpty()) {
            return ConsentData(consentStatus,
                    serverConfig.companies, false, currentYear)
        }

        companyIds = npaPublishers
                .flatMap {
                    it.companyIds
                }
                .toHashSet()

        companies = serverConfig.companies
                .filter { it.companyId in companyIds }

        return ConsentData(consentStatus, companies, true, currentYear)
    }
}