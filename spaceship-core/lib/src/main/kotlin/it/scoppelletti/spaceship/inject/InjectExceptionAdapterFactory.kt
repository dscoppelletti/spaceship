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

package it.scoppelletti.spaceship.inject

import it.scoppelletti.spaceship.widget.DefaultExceptionAdapter
import it.scoppelletti.spaceship.widget.ExceptionAdapter
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

/**
 * Implementation of the `ExceptionAdapter.Factory` interface.
 *
 * @since 1.0.0
 *
 * @constructor          Constructor.
 * @param       creators Map to associate a `Throwable` class with a
 *                       `Provider<ExceptionAdapter>` dependency.
 */
@Singleton
public class InjectExceptionAdapterFactory @Inject constructor(
        private val creators: Map<Class<out Throwable>,
                @JvmSuppressWildcards Provider<ExceptionAdapter<*>>>
) : ExceptionAdapter.Factory {

    override fun <T : Throwable> create(
            exClass: Class<T>
    ) : ExceptionAdapter<*> {
        val creator: Provider<ExceptionAdapter<*>>?

        creator = creators[exClass] ?:
                creators.entries.firstOrNull {
                    it.key.isAssignableFrom(exClass)
                }?.value

        return creator?.get() ?: DefaultExceptionAdapter()
    }
}