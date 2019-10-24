/*
 * Copyright (C) 2019 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

package it.scoppelletti.spaceship.ads.i18n

import it.scoppelletti.spaceship.ads.R
import it.scoppelletti.spaceship.i18n.AndroidResourceMessageSpec
import it.scoppelletti.spaceship.i18n.MessageSpec

/**
 * String resources.
 *
 * @since 1.0.0
 */
public object AdsMessages {

    public fun errorConfig(): MessageSpec =
            AndroidResourceMessageSpec(R.string.it_scoppelletti_ads_err_config)

    public fun errorLookupFailed(lookupFailedIds: List<String>): MessageSpec =
            AndroidResourceMessageSpec(
                    R.string.it_scoppelletti_ads_err_lookupFailed, arrayOf(
                    lookupFailedIds.toTypedArray().contentToString()))

    public fun errorNotFound(notFoundIds: List<String>): MessageSpec =
            AndroidResourceMessageSpec(
                    R.string.it_scoppelletti_ads_err_notFound,
                    arrayOf(notFoundIds.toTypedArray().contentToString()))

    public fun errorPublisher(
            lookupFailedIds: List<String>,
            notFoundIds: List<String>
    ): MessageSpec = AndroidResourceMessageSpec(
            R.string.it_scoppelletti_ads_err_publisher, arrayOf(
            lookupFailedIds.toTypedArray().contentToString(),
            notFoundIds.toTypedArray().contentToString()))

    public fun errorUserNotLocatedInEea(): MessageSpec =
            AndroidResourceMessageSpec(
                    R.string.it_scoppelletti_ads_err_notInEea)
}
