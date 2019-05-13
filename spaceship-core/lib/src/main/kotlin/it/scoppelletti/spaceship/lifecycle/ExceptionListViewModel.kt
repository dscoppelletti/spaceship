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
import it.scoppelletti.spaceship.widget.ExceptionAdapter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

/**
 * `ViewModel` of an exception chain.
 *
 * @since 1.0.0
 *
 * @property state Collection of exceptions.
 */
public class ExceptionListViewModel internal constructor(
        dispatcher: CoroutineDispatcher,
        private val outerEx: Throwable,
        private val adapterFactory: ExceptionAdapter.Factory
): ViewModel(), CoroutineScope {

    private val job = Job()
    private val _state = MutableLiveData<ExceptionListState>()
    public val state: LiveData<ExceptionListState> = _state

    public override val coroutineContext: CoroutineContext = dispatcher + job

    /**
     * Loads the chain of an exception.
     */
    public fun load() = launch {
        val exList: MutableList<ExceptionItemState>

        if (!_state.value?.exList.isNullOrEmpty()) {
            return@launch
        }

        exList = mutableListOf()
        withContext(Dispatchers.Default) {
            var ex: Throwable? = outerEx
            var adapter: ExceptionAdapter<*>

            while (ex != null) {
                if (!isActive) {
                    return@withContext
                }

                adapter = adapterFactory.create(ex.javaClass)
                exList.add(ExceptionItemState(ex, adapter))

                ex = ex.cause
            }
        }

        _state.value = ExceptionListState(exList)
    }

    override fun onCleared() {
        job.cancel()
        super.onCleared()
    }
}

/**
 * State of an exception chain.
 *
 * @since 1.0.0
 *
 * @property exList Collection of exceptions.
 */
public data class ExceptionListState(
        public val exList: List<ExceptionItemState>
)

/**
 * State of an exception.
 *
 * @since 1.0.0
 *
 * @property ex      Exception.
 * @property adapter Renders an exception as an item in a `ListView` control.
 */
public data class ExceptionItemState(
        public val ex: Throwable,
        public val adapter: ExceptionAdapter<*>
)

