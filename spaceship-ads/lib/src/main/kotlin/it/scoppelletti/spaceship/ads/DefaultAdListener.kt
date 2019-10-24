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

import com.google.android.gms.ads.AdListener
import mu.KotlinLogging

/**
 * Default implementation of `AdListener` interface.
 *
 * @since 1.0.0
 */
public open class DefaultAdListener : AdListener() {

    /**
     * An Ad has finished loading.
     */
    override fun onAdLoaded() {
        logger.debug("Ad loaded.")
    }

    /**
     * An Ad request has failed.
     *
     * @param errorCode The error code.
     */
    override fun onAdFailedToLoad(errorCode: Int) {
        logger.error { "Ad request failed (errorCode=$errorCode)." }
    }

    /**
     * The user has tapped on an Ad.
     *
     * The Ad opens an overlay that covers the screen, so you may suspend some
     * activities of the App.
     */
    override fun onAdOpened() {
        // The ad opens an overlay that covers the screen
        logger.debug("The user tapped on an Ad.")
    }

    /**
     * The user has left the App by clicking on an Ad.
     *
     * This method is invoked after [onAdOpened].
     */
    override fun onAdLeftApplication() {
        logger.debug("The user left the app clicking on an ad.")
    }

    /**
     * The user is returning to the App.
     *
     * You may resume some suspended activities or anyway make the App ready for
     * interaction.
     */
    override fun onAdClosed() {
        logger.debug("The user is returning to the app.")
    }

    override fun onAdClicked() {
        logger.debug("onAdClicked.")
    }

    override fun onAdImpression() {
        logger.debug("onAdImpression.")
    }

    private companion object {
        val logger = KotlinLogging.logger { }
    }
}
