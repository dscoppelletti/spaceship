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


@file:Suppress("JoinDeclarationAndAssignment", "RedundantVisibilityModifier")

package it.scoppelletti.spaceship.lifecycle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

/**
 * Implementation of the `ViewModelProviderEx` interface for dependency
 * injection in `ViewModel` objects.
 *
 * @since 1.0.0
 */
@Singleton
public class DefaultViewModelProviderEx @Inject constructor(
        private val creators: Map<Class<out ViewModel>,
                @JvmSuppressWildcards
                Provider<ViewModelProviderEx.Factory>>
) : ViewModelProviderEx {

    override fun <T : ViewModel> get(
            owner: ViewModelStoreOwner,
            modelClass: Class<T>
    ): T {
        val factory: ViewModelProviderEx.Factory

        factory = creators[modelClass]?.get() ?: throw IllegalArgumentException(
                    "Unknown model class $modelClass.")
        return factory.create(owner as SavedStateRegistryOwner, null)
    }
}
