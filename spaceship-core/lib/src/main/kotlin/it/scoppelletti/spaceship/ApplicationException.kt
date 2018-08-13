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

package it.scoppelletti.spaceship

import android.support.annotation.StringRes
import it.scoppelletti.spaceship.types.trimRaw
import java.lang.reflect.InvocationTargetException

/**
 * Application exception.
 *
 * @since                   1.0.0
 * @property messageBuilder The message.
 * @property titleId        The title as a string resource ID.
 */
public class ApplicationException private constructor(
        builder: ApplicationException.Builder
) : RuntimeException() {

    public val messageBuilder: MessageBuilder

    @StringRes
    public val titleId: Int

    init {
        messageBuilder = builder.messageBuilder!!
        titleId = builder.titleId
        if (builder.cause != null) {
            initCause(builder.cause)
        }
    }

    override val message: String?
        get() = toString()

    override fun toString() = """
        |ApplicationException(messageBuilder=$messageBuilder,
        |titleId=$titleId)""".trimRaw()

    /**
     * Builds an `ApplicationException` instance.
     *
     * @since            1.0.0
     * @property titleId The title as a string resource ID.
     * @property cause   The original exception.
     */
    @MessageBuilder.Dsl
    @ApplicationException.Dsl
    public class Builder internal constructor() {

        @StringRes public var titleId: Int = android.R.string.dialog_alert_title
        public var cause: Throwable? = null
        internal var messageBuilder: MessageBuilder? = null

        /**
         * Defines the `MessageBuilder` object.
         *
         * @param  messageId The message as a string resource ID.
         * @param  init      The initializiation block.
         * @return           The new object.
         */
        public fun message(
                @StringRes messageId: Int,
                init: MessageBuilder.() -> Unit = { }
        ): MessageBuilder {
            messageBuilder = MessageBuilder.make(messageId, init)
            return messageBuilder!!
        }

        /**
         * Builds a new `ApplicationException` instance.
         *
         * @return The new object.
         */
        internal fun build(): ApplicationException {
            if (messageBuilder == null) {
                throw NullPointerException("Missing the MessageBuilder object.")
            }

            return ApplicationException(this)
        }
    }

    /**
     * Marks the `ApplicationException` DSL's objects.
     *
     * @since 1.0.0
     */
    @DslMarker
    public annotation class Dsl
}

/**
 * Creates a new [ApplicationException] instance.
 *
 * @param  init The initialization block.
 * @return      The new object.
 * @since       1.0.0
 */
public fun applicationException(
        init: ApplicationException.Builder.() -> Unit
): ApplicationException = ApplicationException.Builder().apply(init).build()

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
