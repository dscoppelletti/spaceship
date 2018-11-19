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

import androidx.annotation.StringRes
import it.scoppelletti.spaceship.types.trimRaw
import java.lang.reflect.InvocationTargetException

/**
 * Application exception.
 *
 * @since 1.0.0
 *
 * @property messageBuilder Message.
 * @property titleBuilder   Title.
 */
public class ApplicationException private constructor(
        builder: ApplicationException.Builder
) : RuntimeException() {

    public val messageBuilder: MessageBuilder
    public val titleBuilder: MessageBuilder

    init {
        messageBuilder = builder.messageBuilder ?:
                throw NullPointerException("Missing the messageBuilder object.")

        titleBuilder = builder.titleBuilder ?:
                MessageBuilder.make(android.R.string.dialog_alert_title) {
                }

        if (builder.cause != null) {
            initCause(builder.cause)
        }
    }

    override val message: String?
        get() = toString()

    override fun toString() = """
        |ApplicationException(messageBuilder=$messageBuilder,
        |titleId=$titleBuilder)""".trimRaw()

    /**
     * Builds an `ApplicationException` instance.
     *
     * @since 1.0.0
     *
     * @property cause Original exception.
     */
    @MessageBuilder.Dsl
    @ApplicationException.Dsl
    public class Builder internal constructor() {

        public var cause: Throwable? = null
        internal var messageBuilder: MessageBuilder? = null
        internal var titleBuilder: MessageBuilder? = null

        /**
         * Defines the message.
         *
         * @param  messageId Message as a string resource ID.
         * @param  init      Initializiation block.
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
         * Defines the message.
         *
         * @param  message Message.
         * @param  init    Initializiation block.
         * @return         The new object.
         */
        public fun message(
                message: String,
                init: MessageBuilder.() -> Unit = { }
        ): MessageBuilder {
            messageBuilder = MessageBuilder.make(message, init)
            return messageBuilder!!
        }

        /**
         * Defines the title.
         *
         * @param  titleId Title as a string resource ID.
         * @param  init    Initialization block.
         * @return         The new object.
         */
        public fun title(
                @StringRes titleId: Int,
                init: MessageBuilder.() -> Unit = { }
        ): MessageBuilder {
            titleBuilder = MessageBuilder.make(titleId, init)
            return titleBuilder!!
        }

        /**
         * Defines the title.
         *
         * @param  title Title.
         * @param  init  Initialization block.
         * @return       The new object.
         */
        public fun title(
                title: String,
                init: MessageBuilder.() -> Unit = { }
        ): MessageBuilder {
            titleBuilder = MessageBuilder.make(title, init)
            return titleBuilder!!
        }

        /**
         * Builds a new `ApplicationException` instance.
         *
         * @return The new object.
         */
        internal fun build(): ApplicationException {
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
 * @param  init Initialization block.
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
 * @receiver An exception.
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
 * @receiver An exception.
 * @return   The message.
 */
private fun Throwable.toMessageImpl(): String {
    var msg: String? = localizedMessage

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

    msg = this.javaClass.canonicalName
    if (!msg.isNullOrBlank()) {
        return msg
    }

    return this.javaClass.name
}
