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

package it.scoppelletti.spaceship.gms

import com.google.android.gms.common.api.Status
import it.scoppelletti.spaceship.types.joinLines
import mu.KotlinLogging

private val logger = KotlinLogging.logger { }

/**
 * Exception from Google API.
 *
 * @since 1.0.0
 *
 * @property statusCode    Status of the operation.
 * @property statusMessage Descriptive status of the operation.
 * @property isCanceled    Indicates whether the operation is canceled
 * @property isInterrupted Indicates whether the operation is interrupted.
 */
public class GmsException(
        public val statusCode: Int,
        public val statusMessage: String?,
        public val isCanceled: Boolean,
        public val isInterrupted: Boolean
): RuntimeException() {

    override val message: String?
        get() = toString()

    override fun toString(): String = """GmsException(statusCode=$statusCode,
        |statusMessage=$statusMessage,isCanceled=$isCanceled,
        |isInterrupted=$isInterrupted)""".trimMargin().joinLines()
}

/**
 * Converts the results of a work to an exception.
 *
 * @receiver The original results.
 * @return   The corresponding exception.
 * @since    1.0.0
 */
@Suppress("unused")
public fun Status.toException(): GmsException {
    if (this.isSuccess || this.hasResolution()) {
        logger.warn { "${toString(this)} converted to GmsException." }
    }

    return GmsException(this.statusCode, this.statusMessage, this.isCanceled,
            this.isInterrupted)
}

private fun toString(status: Status) =
        """Status(statusCode=${status.statusCode},
            |message=${status.statusMessage},
            |hasResolution=${status.hasResolution()},
            |isCanceled=${status.isCanceled},
            |isInterrupted=${status.isInterrupted},
            |isSuccess=${status.isSuccess})""".trimMargin().joinLines()
