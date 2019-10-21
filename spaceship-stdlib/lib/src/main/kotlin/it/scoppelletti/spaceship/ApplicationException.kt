/*
 * Copyright (C) 2008-2018 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

@file:Suppress("RedundantVisibilityModifier", "unused")

package it.scoppelletti.spaceship

import it.scoppelletti.spaceship.i18n.MessageSpec
import it.scoppelletti.spaceship.types.StringExt

/**
 * Application exception.
 *
 * @since 1.0.0
 *
 * @property messageSpec Message specification.
 */
public class ApplicationException constructor(

        @Suppress("WeakerAccess")
        public val messageSpec: MessageSpec,

        override val cause: Throwable? = null
) : RuntimeException() {

    override val message: String?
        get() = toString()

    override fun toString(): String = "ApplicationException($messageSpec)"
}

/**
 * Returns the message of an exception.
 *
 * The message is obtained by the following fallbacks:
 *
 * 1. The `localizedMessage` property of the exception object.
 * 1. The `message` property of the exception object.
 * 1. The fully qualified name of the exception class.
 *
 * @receiver An exception.
 * @return   The message.
 * @since    1.0.0
 */
public fun Throwable?.toMessage(): String {
    if (this == null) {
        return "null"
    }

    var msg: String? = this.localizedMessage

    if (!msg.isNullOrBlank()) {
        return msg
    }

    msg = this.message
    if (!msg.isNullOrBlank()) {
        return msg
    }

    msg = this.toString()
    if (!msg.isNullOrBlank()) {
        return msg
    }

    return StringExt.EMPTY
}
