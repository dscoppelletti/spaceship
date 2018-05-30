/*
 * Copyright (C) 2008-2017 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

package it.scoppelletti.spaceship

import android.support.annotation.StringRes
import java.lang.reflect.InvocationTargetException

/**
 * Application exception.
 *
 * @since                     1.0.0
 * @property messageId        The message as a string resource ID.
 * @property messageArguments The arguments to build the message.
 * @property titleId          The title as a string resource ID.
 *
 * @constructor Constructor.
 */
public class ApplicationException(
        @StringRes val messageId: Int,
        val messageArguments: Array<out Any>? = null,
        @StringRes val titleId: Int = android.R.string.dialog_alert_title,
        override val cause: Throwable? = null
) : RuntimeException() {

    override val message: String?
        get() = toString()

    override fun toString() = """${javaClass.name}(messageId=$messageId,
messageArguments=${messageArguments?.contentToString()},
titleId=$titleId)""".replace('\n', ' ')
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
 * @receiver The exception.
 * @return   The message.
 * @since    1.0.0
 */
public fun Throwable?.toMessage(): String {
    val target: Throwable
    var msg: String

    if (this == null) {
        return "null"
    }

    if (this is InvocationTargetException) {
        target = cause ?: this
    } else {
        target = this
    }

    msg = target.toMessageImpl()

    if (target is ClassCastException) {
        msg = "Class $msg not found."
    }

    return msg
}

/**
 * Returns the message of an exception.
 *
 * @receiver The exception.
 * @return   The message.
 */
private fun Throwable.toMessageImpl(): String {
    var msg: String? = localizedMessage

    if (!msg.isNullOrBlank()) {
        return msg as String
    }

    msg = this.message
    if (!msg.isNullOrBlank()) {
        return msg as String
    }

    msg = this.toString()
    if (!msg.isNullOrBlank()) {
        return msg
    }

    return javaClass.canonicalName
}
