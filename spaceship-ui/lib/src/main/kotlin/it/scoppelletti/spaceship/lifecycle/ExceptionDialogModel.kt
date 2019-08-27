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

package it.scoppelletti.spaceship.lifecycle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.scoppelletti.spaceship.app.ExceptionDialogFragment
import it.scoppelletti.spaceship.StdlibExt
import it.scoppelletti.spaceship.widget.ExceptionItem
import it.scoppelletti.spaceship.widget.ExceptionMapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

/**
 * `ViewModel` of an [ExceptionDialogFragment] fragment.
 *
 * @since 1.0.0
 *
 * @property state State.
 */
public class ExceptionDialogModel @Inject constructor(

        @Named(StdlibExt.DEP_MAINDISPATCHER)
        dispatcher: CoroutineDispatcher,

        private val exMapper: ExceptionMapper
): ViewModel() {

    private val scope = CoroutineScope(dispatcher + Job())
    private val _state = MutableLiveData<ExceptionDialogState>()
    public val state: LiveData<ExceptionDialogState> = _state

    /**
     * Loads the state.
     *
     * @param exState Exception state.
     */
    public fun load(exState: ExceptionActivityState) = scope.launch {
        val exList: List<ExceptionItem>

        exList = withContext(Dispatchers.Default) {
            val list: MutableList<ExceptionItem>
            var ex: Throwable? = exState.ex

            list = mutableListOf()

            while (ex != null) {
                if (!isActive) {
                    break
                }

                list.add(exMapper.map(ex))
                ex = ex.cause
            }

            list
        }

        _state.value = ExceptionDialogState(exList)
    }

    override fun onCleared() {
        scope.cancel()
        super.onCleared()
    }
}

/**
 * State of an [ExceptionDialogFragment] fragment.
 *
 * @since 1.0.0
 *
 * @property exList Collection of exceptions.
 */
public data class ExceptionDialogState(
        public val exList: List<ExceptionItem>
)

