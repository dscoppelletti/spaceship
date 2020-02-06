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

package it.scoppelletti.spaceship

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import mu.KotlinLogging
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Default implementation of the `ExceptionLogger` interface.
 *
 * @since 1.0.0
 */
@Singleton
public class DefaultExceptionLogger @Inject constructor(
        private val handlers: Set<
                @JvmSuppressWildcards ExceptionLoggerHandler<*>>
) : ExceptionLogger {

    override fun log(ex: Throwable) {
        doLog(ex)
    }

    private fun doLog(ex: Throwable) = GlobalScope.launch(Dispatchers.IO) {
        handlers.forEach {
            val handler: ExceptionLoggerHandler<Throwable>

            if (!isActive) {
                return@launch
            }

            @Suppress("UNCHECKED_CAST")
            handler = it as ExceptionLoggerHandler<Throwable>

            try {
                handler.log(ex)
            } catch (logEx: Exception) {
                logger.error(logEx) { "Failure in logger $handler." }
            }
        }
    }

    private companion object {
        val logger = KotlinLogging.logger { }
    }
}
