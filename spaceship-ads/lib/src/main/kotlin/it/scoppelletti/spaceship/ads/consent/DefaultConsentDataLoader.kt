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

package it.scoppelletti.spaceship.ads.consent

import androidx.annotation.WorkerThread
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import it.scoppelletti.spaceship.ads.AdsConfig
import it.scoppelletti.spaceship.ads.R
import it.scoppelletti.spaceship.ads.model.AdNetworkLookupResponse
import it.scoppelletti.spaceship.ads.model.ConsentData
import it.scoppelletti.spaceship.ads.model.ServerResponse
import it.scoppelletti.spaceship.applicationException
import it.scoppelletti.spaceship.http.toHttpApplicationException
import retrofit2.HttpException
import java.util.Calendar
import javax.inject.Inject

/**
 * Default implementation of the `ConsentDataLoader` interface.
 *
 * @since 1.0.0
 *
 * @constructor                  Constructor.
 * @param       consentDataStore Local store for the `ConsentData` object.
 * @param       adService        Client interface to Ad Service.
 * @param       adsConfig        Configuration of AdMob.
 */
@WorkerThread
public class DefaultConsentDataLoader @Inject constructor(
        private val consentDataStore: ConsentDataStore,
        private val adService: AdService,
        private val adsConfig: AdsConfig
) : ConsentDataLoader {
    private val currentYear: Int

    init {
        currentYear = Calendar.getInstance().get(Calendar.YEAR)
    }

    override fun load(): Single<ConsentData> =
            Single.zip(consentDataStore.load(), getServerConfig(),
                    BiFunction<ConsentData, ConsentData, ConsentData> {
                consentData, serverConfig ->
                merge(consentData, serverConfig)
            })

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
     * @return The new observable.
     */
    private fun getServerConfig(): Single<ConsentData> {
        return adService.getConfig(adsConfig.publisherId,
                adsConfig.debugGeography.code)
                .onErrorResumeNext { ex ->
                    Single.error(applicationException {
                        message(R.string.it_scoppelletti_ads_err_user)
                        cause = (ex as? HttpException)
                                ?.toHttpApplicationException() ?: ex
                    })
                }
                .flatMap { serverConfig ->
                    collectLookupFailedIds(serverConfig)
                            .map { lookupFailedIds ->
                                Pair(serverConfig, lookupFailedIds)
                            }
                }
                .flatMap { (serverConfig, lookupFailedIds) ->
                    collectNotFoundIds(serverConfig)
                            .flatMap { notFoundIds ->
                                validatePublisherIds(serverConfig,
                                        lookupFailedIds, notFoundIds)
                            }
                }
                .flatMap { serverConfig ->
                    collectNPAPublishers(serverConfig)
                            .map { npaPublishers ->
                                Pair(serverConfig, npaPublishers)
                            }
                }
                .flatMap { (serverConfig, npaPublishers) ->
                    collectNPAProviders(serverConfig, npaPublishers)
                }
    }

    /**
     * Collects the ID of the publishers with any network error.
     *
     * @param  serverConfig Server configuration.
     * @return              The new observable.
     */
    private fun collectLookupFailedIds(
            serverConfig: ServerResponse
    ): Single<List<String>> {
        if (!serverConfig.isRequestLocationInEeaOrUnknown) {
            return Single.just(emptyList())
        }

        return Observable.fromIterable(serverConfig.adNetworkLookupResponses)
                .filter { it.lookupFailed }
                .map { it.networkId }
                .toList()
    }

    /**
     * Collects the ID of the not found publishers.
     *
     * @param  serverConfig Server configuration.
     * @return              The new observable.
     */
    private fun collectNotFoundIds(
            serverConfig: ServerResponse
    ): Single<List<String>> {
        if (!serverConfig.isRequestLocationInEeaOrUnknown) {
            return Single.just(emptyList())
        }

        return Observable.fromIterable(serverConfig.adNetworkLookupResponses)
                .filter { it.notFound }
                .map { it.networkId }
                .toList()
    }

    /**
     * Validates the publishers.
     *
     * @param  serverConfig    Server configuration.
     * @param  lookupFailedIds Publishers with any network error.
     * @param  notFoundIds     Not found publishers.
     * @return                 The new observable.
     */
    private fun validatePublisherIds(
            serverConfig: ServerResponse,
            lookupFailedIds: List<String>,
            notFoundIds: List<String>
    ): Single<ServerResponse> {
        if (!lookupFailedIds.isEmpty() && !notFoundIds.isEmpty())      {
            return Single.error<ServerResponse>(applicationException {
                message(R.string.it_scoppelletti_ads_err_publisher) {
                    arguments {
                        add(lookupFailedIds.toTypedArray().contentToString())
                        add(notFoundIds.toTypedArray().contentToString())
                    }
                }
            })
        }

        if (!lookupFailedIds.isEmpty()) {
            return Single.error<ServerResponse>(applicationException {
                message(R.string.it_scoppelletti_ads_err_lookupFailed) {
                    arguments {
                        add(lookupFailedIds.toTypedArray().contentToString())
                    }
                }
            })
        }

        if (!notFoundIds.isEmpty())      {
            return Single.error<ServerResponse>(applicationException {
                message(R.string.it_scoppelletti_ads_err_notFound) {
                    arguments {
                        add(notFoundIds.toTypedArray().contentToString())
                    }
                }
            })
        }

        return Single.just(serverConfig)
    }

    /**
     * Collects the publishers with non personalized Ad providers.
     *
     * @param  serverConfig Server configuration.
     * @return              The new observable.
     */
    private fun collectNPAPublishers(
            serverConfig: ServerResponse
    ): Single<List<AdNetworkLookupResponse>> {
        return Observable.fromIterable(serverConfig.adNetworkLookupResponses)
                .filter { it.isNPA }
                .toList()
    }

    /**
     * Collects the non personalized Ad providers.
     *
     * @param  serverConfig  Server configuration.
     * @param  npaPublishers Publishers with non personalized Ad providers.
     * @return               The new observable.
     */
    private fun collectNPAProviders(
            serverConfig: ServerResponse,
            npaPublishers: List<AdNetworkLookupResponse>
    ): Single<ConsentData> {
        val consentStatus: ConsentStatus

        consentStatus = if (serverConfig.isRequestLocationInEeaOrUnknown)
            ConsentStatus.UNKNOWN else ConsentStatus.NOT_IN_EEA

        if (npaPublishers.isEmpty()) {
            return Single.just(ConsentData(consentStatus,
                    serverConfig.companies, false, currentYear))
        }

        return Observable.fromIterable(npaPublishers)
                .concatMapIterable { it.companyIds }
                .distinct()
                .toList()
                .flatMapObservable { ids ->
                    Observable.fromIterable(serverConfig.companies)
                            .filter { it.companyId in ids }
                }
                .toList()
                .map {
                    ConsentData(consentStatus, it, true, currentYear)
                }
    }
}