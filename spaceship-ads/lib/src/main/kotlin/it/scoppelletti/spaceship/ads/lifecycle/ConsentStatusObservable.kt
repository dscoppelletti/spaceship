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

package it.scoppelletti.spaceship.ads.lifecycle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import it.scoppelletti.spaceship.ads.consent.ConsentStatus

/**
 * Status for consent to receive perzonalized advertising.
 *
 * @since 1.0.0
 *
 * @property status The observable status.
 */
public object ConsentStatusObservable {

    @Suppress("ObjectPropertyName")
    private val _status = MutableLiveData<ConsentStatus>()

    public val status: LiveData<ConsentStatus> = _status

    /**
     * Sets the status.
     *
     * @param value The value.
     */
    internal fun setStatus(value: ConsentStatus) {
        _status.value = value
    }
}
