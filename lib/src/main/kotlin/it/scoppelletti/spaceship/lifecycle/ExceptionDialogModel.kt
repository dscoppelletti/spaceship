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

package it.scoppelletti.spaceship.lifecycle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.scoppelletti.spaceship.app.AppExt
import it.scoppelletti.spaceship.app.ExceptionDialogFragment
import it.scoppelletti.spaceship.widget.ExceptionItem
import it.scoppelletti.spaceship.widget.ExceptionMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * `ViewModel` of an [ExceptionDialogFragment] view.
 *
 * @since 1.0.0
 *
 * @property state State of the view.
 */
public class ExceptionDialogModel(
        private val exMapper: ExceptionMapper,
        private val handle: SavedStateHandle
): ViewModel() {

    private val _state = MutableLiveData<ExceptionDialogState>()
    public val state: LiveData<ExceptionDialogState> = _state

    init {
        val item: ExceptionItem? = handle[PROP_ITEM]
        if (item != null) {
            _state.value = ExceptionDialogState(listOf(item))
        }
    }

    /**
     * Loads the state.
     *
     * @param source Exception.
     */
    public fun load(source: Throwable): Job = viewModelScope.launch {
        val exList: List<ExceptionItem>

        exList = withContext(Dispatchers.Default) {
            val list = mutableListOf<ExceptionItem>()
            var ex: Throwable? = source

            while (ex != null) {
                if (!isActive) {
                    break
                }

                list.add(exMapper.map(ex))
                ex = ex.cause
            }

            list
        }

        if (exList.isNotEmpty()) {
            handle[PROP_ITEM] = exList[0]
        }

        _state.value = ExceptionDialogState(exList)
    }

    private companion object {
        const val PROP_ITEM = AppExt.PROP_ITEM
    }
}

/**
 * State of an [ExceptionDialogFragment] view.
 *
 * @since 1.0.0
 *
 * @property exList Collection of exceptions.
 */
public data class ExceptionDialogState(
        public val exList: List<ExceptionItem>
)

/**
 * Implementation of `ViewModelProviderEx.Factory` interface for
 * [ExceptionDialogModel] class.
 *
 * @since 1.0.0
 */
public class ExceptionDialogModelFactory @Inject constructor(
        private val exMapper: ExceptionMapper
) : ViewModelProviderEx.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(handle: SavedStateHandle): T =
            ExceptionDialogModel(exMapper, handle) as T
}

