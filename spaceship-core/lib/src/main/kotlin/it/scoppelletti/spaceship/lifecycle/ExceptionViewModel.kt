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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import it.scoppelletti.spaceship.ExceptionLogger
import it.scoppelletti.spaceship.app.ExceptionDialogFragment
import it.scoppelletti.spaceship.widget.ExceptionAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import mu.KotlinLogging

/**
 * `ViewModel` used by an activity to pass an exception to an
 * [ExceptionDialogFragment] fragment.
 *
 * @since 1.0.0
 */
public class ExceptionViewModel private constructor(
        private val exceptionLoggers: Set<ExceptionLogger>,
        private val adapterFactory: ExceptionAdapter.Factory
): ViewModel(), ViewModelProvider.Factory {

    private var _ex: Throwable? = null

    /**
     * Sets the exception.
     *
     * @param ex Exception.
     */
    public fun setException(ex: Throwable) {
        _ex = ex
        log(ex)
    }

    /**
     * Logs an exception.
     *
     * @param ex Exception.
     */
    private fun log(ex: Throwable) = GlobalScope.launch(Dispatchers.IO) {
        exceptionLoggers.forEach { exLogger ->
            if (!isActive) {
                return@launch
            }

            try {
                exLogger.log(ex)
            } catch (logEx: Exception) {
                logger.error(logEx) { "Failure in logger $exLogger." }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            (ExceptionListViewModel(Dispatchers.Main, _ex!!,
                    adapterFactory) as T)
                    .also {
                        _ex = null
                    }

    /**
     * Creates an `ExceptionViewModel` instance.
     *
     * @since 1.0.0
     *
     * @constructor                  Constructor.
     * @param       exceptionLoggers Logs an exception.
     * @param       adapterFactory   Creates an adapter for an exception class.
     */
    public class Factory(
            private val exceptionLoggers: Set<ExceptionLogger>,
            private val adapterFactory: ExceptionAdapter.Factory
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            ExceptionViewModel(exceptionLoggers, adapterFactory) as T
    }

    private companion object {
        val logger = KotlinLogging.logger {}
    }
}
