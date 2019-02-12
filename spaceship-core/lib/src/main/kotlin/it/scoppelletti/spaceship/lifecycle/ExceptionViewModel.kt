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

import androidx.lifecycle.ViewModel
import io.reactivex.schedulers.Schedulers
import it.scoppelletti.spaceship.ExceptionLogger
import it.scoppelletti.spaceship.app.ExceptionDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.asCoroutineDispatcher
import mu.KLogger
import mu.KotlinLogging
import java.lang.Exception
import javax.inject.Inject

/**
 * `ViewModel` used by an activity to pass an exception to an
 * [ExceptionDialogFragment] fragment.
 *
 * @since 1.0.0
 *
 * @property ex Exception.
 *
 * @constructor                  Constructor.
 * @params      exceptionLoggers Logs an exception.
 */
@JvmSuppressWildcards
public class ExceptionViewModel @Inject constructor(
        private val exceptionLoggers: Set<ExceptionLogger>
): ViewModel() {

    public var ex: Throwable? = null

    /**
     * Logs the exception.
     */
    public fun log() {
        ex?.let { ex0 ->
            CoroutineScope(Schedulers.io().asCoroutineDispatcher()).launch {
                exceptionLoggers.forEach { exLogger ->
                    try {
                        exLogger.log(ex0)
                    } catch (logEx: Exception) {
                        logger.error(logEx) { "Failure in logger $exLogger." }
                    }
                }
            }
        }
    }

    private companion object {
        val logger: KLogger = KotlinLogging.logger {}
    }
}
