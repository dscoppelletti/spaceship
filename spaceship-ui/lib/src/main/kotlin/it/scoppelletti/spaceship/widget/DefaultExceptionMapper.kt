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

package it.scoppelletti.spaceship.widget

import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

/**
 * Default implementation of the `ExceptionMapper` interface.
 *
 * @since 1.0.0
 */
@Singleton
public class DefaultExceptionMapper @Inject constructor(
        private val handlers: Map<Class<out Throwable>,
                @JvmSuppressWildcards Provider<ExceptionMapperHandler<*>>>
) : ExceptionMapper {

    override suspend fun map(ex: Throwable) : ExceptionItem {
        val handler: ExceptionMapperHandler<Throwable>
        val provider: Provider<ExceptionMapperHandler<*>>?

        provider = handlers[ex.javaClass] ?:
                handlers.entries.firstOrNull {
                    it.key.isAssignableFrom(ex.javaClass)
                }?.value

        @Suppress("UNCHECKED_CAST")
        handler = provider?.get() as ExceptionMapperHandler<Throwable>? ?:
                DefaultExceptionMapperHandler()

        return handler.map(ex)
    }
}