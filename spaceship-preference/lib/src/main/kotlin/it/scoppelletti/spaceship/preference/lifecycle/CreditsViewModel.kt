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

package it.scoppelletti.spaceship.preference.lifecycle

import androidx.annotation.XmlRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.scoppelletti.spaceship.StdlibExt
import it.scoppelletti.spaceship.lifecycle.SingleEvent
import it.scoppelletti.spaceship.preference.credit.CreditsLoader
import it.scoppelletti.spaceship.preference.model.Credit
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

/**
 * ViewModel of the `CreditsActivity` view.
 *
 * @see   it.scoppelletti.spaceship.preference.CreditsActivity
 * @since 1.0.0
 *
 * @property state State of the view.
 *
 * @constructor            Constructor.
 * @param       dispatcher Coroutine dispatcher.
 * @param       loader     Loads the credits.
 */
public class CreditsViewModel @Inject constructor(
        private val loader: CreditsLoader,

        @Named(StdlibExt.DEP_MAINDISPATCHER)
        dispatcher: CoroutineDispatcher
): ViewModel() {

    private val scope = CoroutineScope(dispatcher + Job())
    private val _state = MutableLiveData<CreditsState>()

    public val state: LiveData<CreditsState> = _state

    /**
     * Loads the credits.
     *
     * @param creditId ID of the XML resource.
     */
    public fun load(@XmlRes creditId: Int) = scope.launch {
        val items: List<Credit>

        if (_state.value != null) {
            return@launch
        }

        try {
            _state.value = CreditsState(emptyList(), true, null)
            items = loader.load(creditId)
            _state.value = CreditsState.create(items)
        } catch (ex: CancellationException) {
            throw ex
        } catch (ex: Exception) {
            _state.value = _state.value?.withError(ex)
        }
    }

    override fun onCleared() {
        scope.cancel()
        super.onCleared()
    }
}

/**
 * State of the `CreditsActivity` view.
 *
 * @see   it.scoppelletti.spaceship.preference.CreditsActivity
 * @since 1.0.0
 *
 * @property items   Collection of credits.
 * @property waiting Whether a work is in progress.
 * @property error   Error to show.
 */
public data class CreditsState(
        public val items: List<Credit>,
        public val waiting: Boolean,
        public val error: SingleEvent<Throwable>?
) {

    /**
     * Sets an error to show.
     *
     * @param  ex The error.
     * @return    The new object.
     */
    internal fun withError(ex: Throwable): CreditsState =
            copy(error = SingleEvent(ex), waiting = false)

    internal companion object {

        /**
         * Creates an instance.
         *
         * @param  items Collection of credits.
         * @return       The new object.
         */
        internal fun create(items: List<Credit>): CreditsState =
                CreditsState(items, false, null)
    }
}
