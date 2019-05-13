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

package it.scoppelletti.spaceship.ads.lifecycle

import it.scoppelletti.spaceship.ads.app.ConsentAgeFragment
import it.scoppelletti.spaceship.ads.app.ConsentPromptFragment
import it.scoppelletti.spaceship.ads.app.ConsentPrivacyFragment
import it.scoppelletti.spaceship.ads.app.ConsentReminderFragment
import it.scoppelletti.spaceship.ads.app.ConsentUnderageFragment
import it.scoppelletti.spaceship.ads.model.ConsentData
import it.scoppelletti.spaceship.lifecycle.SingleEvent

/**
 * State of the `AbstractConsentActivity` view.
 *
 * @see   it.scoppelletti.spaceship.ads.app.AbstractConsentActivity
 * @since 1.0.0
 *
 * @property step    Current fragment.
 * @property data    Current data.
 * @property waiting Whether a work is in progress.
 * @property error   Error to show.
 */
public data class ConsentState(
        public val step: Int,
        public val data: ConsentData,
        public val saved: Boolean,
        public val waiting: Boolean,
        public val error: SingleEvent<Throwable>?
) {

    /**
     * Moves to the previous fragment.
     *
     * @return The new object.
     */
    internal fun backStep(): ConsentState {
        val pos: Int

        pos = when (step) {
            ConsentPromptFragment.POS ->
                ConsentAgeFragment.POS
            ConsentPrivacyFragment.POS ->
                ConsentPromptFragment.POS
            ConsentReminderFragment.POS ->
                ConsentPromptFragment.POS
            ConsentUnderageFragment.POS ->
                ConsentAgeFragment.POS
            else ->
                throw IllegalStateException("Property step is $step.")
        }

        return copy(step = pos, waiting = false)
    }

    /**
     * Sets an error to show.
     *
     * @param  ex The error.
     * @return    The new object.
     */
    internal fun withError(ex: Throwable): ConsentState =
            copy(error = SingleEvent(ex), waiting = false)
}
