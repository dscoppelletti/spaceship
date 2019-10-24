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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.scoppelletti.spaceship.StdlibExt
import it.scoppelletti.spaceship.ads.app.ConsentAgeFragment
import it.scoppelletti.spaceship.ads.app.ConsentLoadFragment
import it.scoppelletti.spaceship.ads.consent.ConsentDataLoader
import it.scoppelletti.spaceship.ads.consent.ConsentDataStore
import it.scoppelletti.spaceship.ads.consent.ConsentStatus
import it.scoppelletti.spaceship.ads.model.ConsentData
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.threeten.bp.Clock
import org.threeten.bp.LocalDateTime
import javax.inject.Inject
import javax.inject.Named

/**
 * ViewModel of the `AbstractConsentActivity` view.
 *
 * @see   it.scoppelletti.spaceship.ads.app.AbstractConsentActivity
 * @see   it.scoppelletti.spaceship.ads.model.ConsentData
 * @since 1.0.0
 *
 * @property state State of the view.
 */
public class ConsentViewModel @Inject constructor(
        private val consentDataStore: ConsentDataStore,
        private val consentDataLoader: ConsentDataLoader,

        @Named(StdlibExt.DEP_UTCCLOCK)
        private val clock: Clock,

        @Named(StdlibExt.DEP_MAINDISPATCHER)
        dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val scope = CoroutineScope(dispatcher + Job())
    private val _state = MutableLiveData<ConsentState>()

    public val state: LiveData<ConsentState> = _state

    /**
     * Loads the `ConsentData` object.
     */
    public fun load() = scope.launch {
        val year: Int
        val data: ConsentData

        if (_state.value != null) {
            return@launch
        }

        year = LocalDateTime.now(clock).year
        _state.value = ConsentState(step = ConsentLoadFragment.POS,
                data = ConsentData(year = year), saved = false, waiting = true,
                error = null)

        try {
            data = consentDataLoader.load()
            _state.value = _state.value?.copy(step =
                ConsentAgeFragment.POS, data = data, waiting = false)
        } catch (ex: CancellationException) {
            throw ex
        } catch (ex: Exception) {
            _state.value = _state.value?.withError(ex)
        }
    }

    /**
     * Saves the consent status.
     *
     * @param status The consent status.
     */
    public fun save(status: ConsentStatus) = scope.launch {
        val year: Int
        val data: ConsentData

        if (_state.value == null) {
            return@launch
        }

        _state.value = _state.value?.copy(waiting = true)

        year = LocalDateTime.now(clock).year
        data = (_state.value?.data ?: ConsentData(year = year))
                .copy(consentStatus = status)

        try {
            consentDataStore.save(data)
            _state.value = _state.value?.copy(data = data,
                    saved = true, waiting = false)
        } catch (ex: CancellationException) {
            throw ex
        } catch (ex: Exception) {
            _state.value = _state.value?.withError(ex)
        }
    }

    /**
     * Moves to a fragment.
     *
     * @param value Position of the fragment to select.
     */
    public fun setStep(value: Int) {
        _state.value = _state.value?.copy(step = value, waiting = false)
    }

    /**
     * Moves to the previous fragment.
     *
     * @return Returns `true` if the method succeeded, `false` otherwise.
     */
    public fun backStep(): Boolean {
        try {
            _state.value = _state.value?.backStep()
        } catch (ex: IllegalStateException) {
            return false
        }

        return true
    }

    override fun onCleared() {
        scope.cancel()
        super.onCleared()
    }
}
