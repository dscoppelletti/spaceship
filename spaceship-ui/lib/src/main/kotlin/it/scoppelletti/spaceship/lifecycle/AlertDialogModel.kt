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

@file:Suppress("JoinDeclarationAndAssignment", "RedundantVisibilityModifier")

package it.scoppelletti.spaceship.lifecycle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.scoppelletti.spaceship.StdlibExt
import it.scoppelletti.spaceship.app.AlertDialogFragment
import it.scoppelletti.spaceship.i18n.I18NProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

/**
 * `ViewModel` of an [AlertDialogFragment] fragment.
 *
 * @since 1.0.0
 *
 * @property state State.
 */
public class AlertDialogModel @Inject constructor(
        private val i18nProvider: I18NProvider,

        @Named(StdlibExt.DEP_MAINDISPATCHER)
        dispatcher: CoroutineDispatcher
): ViewModel() {

    private val scope = CoroutineScope(dispatcher + Job())
    private val _state = MutableLiveData<AlertDialogState>()
    public val state: LiveData<AlertDialogState> = _state

    /**
     * Loads the state.
     *
     * @param alertState [AlertDialogFragment] state.
     */
    public fun load(alertState: AlertActivityState) = scope.launch {
        _state.value = AlertDialogState(
                i18nProvider.resolveMessage(alertState.message))
    }

    override fun onCleared() {
        scope.cancel()
        super.onCleared()
    }
}

/**
 * State of an [AlertDialogFragment] fragment.
 *
 * @since 1.0.0
 *
 * @property message Message.
 */
public data class AlertDialogState(
        public val message: String
)

