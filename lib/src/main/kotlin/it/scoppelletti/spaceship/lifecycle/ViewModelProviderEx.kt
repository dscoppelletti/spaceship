/*
 * Copyright (C) 2020 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

package it.scoppelletti.spaceship.lifecycle

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner

/**
 * Provides `ViewModel` objects accessing and contributing to a saved state via
 * `SavedStateHandle` received in the constructor.
 *
 * @since 1.0.0
 */
public interface ViewModelProviderEx {

    /**
     * Gets the `ViewModel` object.
     *
     * @param  owner      Scope of the `ViewModel` object.
     * @param  modelClass The `ViewModel` class.
     * @param  T          The `ViewModel` class.
     * @return            The `ViewModel` object.
     */
    fun <T : ViewModel> get(
            owner: ViewModelStoreOwner,
            modelClass : Class<T>
    ): T

    /**
     * Creates a `ViewModel` object accessing and contributing to a saved state
     * via `SavedStateHandle` received in a constructor.
     *
     * @since 1.0.0
     */
    public interface Factory {

        /**
         * Creates the `ViewModel` object.
         *
         * @param  handle Handle to the saved state.
         * @return        The new object.
         */
        fun <T : ViewModel?> create(handle: SavedStateHandle): T
    }
}
