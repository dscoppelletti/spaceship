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

package it.scoppelletti.spaceship.i18n

import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

/**
 * Default implementation of the `MessageSource` interface.
 *
 * @since 1.0.0
 */
@Singleton
public class DefaultMessageSource @Inject constructor(
        private val handlers: Map<Class<out MessageSpec>,
                @JvmSuppressWildcards Provider<MessageSourceHandler<*>>>
) : MessageSource {

    override suspend fun getMessage(obj: MessageSpec): String {
        val handler: MessageSourceHandler<MessageSpec>?
        val provider: Provider<MessageSourceHandler<*>>?

        provider = handlers[obj.javaClass] ?:
                handlers.entries.firstOrNull {
                    it.key.isAssignableFrom(obj.javaClass)
                }?.value

        @Suppress("UNCHECKED_CAST")
        handler = provider?.get() as MessageSourceHandler<MessageSpec>?

        return handler?.getMessage(obj) ?: obj.toString()
    }
}
