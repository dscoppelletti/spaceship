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
package it.scoppelletti.spaceship

import android.content.res.Resources
import android.support.annotation.StringRes
import it.scoppelletti.spaceship.types.trimRaw

/**
 * Localized message builder.
 *
 * @since              1.0.0
 * @property messageId The message as a string resource ID.
 * @property arguments The format arguments as defined in the `Formatter` class.
 * @property isSimple  Indicates whether the [messageId] is the only one data
 *                     needed to retrieve the message string or not.
 */
@MessageBuilder.Dsl
public class MessageBuilder private constructor(
        @StringRes public val messageId: Int
) {
    private var args: MutableList<Any?>? = null

    public val arguments: List<Any?>?
        get() = args

    public val isSimple: Boolean
        get() = (args == null)

    /**
     * Adds format arguments as defined in the `Formatter` class.
     */
    public fun arguments(init: MessageArguments.() -> Unit): MessageArguments {
        val msgArgs: MessageArguments

        msgArgs = MessageArguments(args).apply(init)
        args = msgArgs.args
        return msgArgs
    }

    /**
     * Builds the message string.
     *
     * @param  resources The object for accessing the application's resources.
     * @return           The message string.
     */
    public fun build(resources: Resources): String =
        if (args == null) resources.getString(messageId) else
            resources.getString(messageId,
                    *(args?.toTypedArray() ?: emptyArray()))

    override fun toString(): String = """MessageBuilder(messageId=$messageId,
        |args=${args?.joinToString()})""".trimRaw()

    public companion object {

        /**
         * Creates a new `MessageBuilder` instance.
         *
         * @param  messageId The message as a string resource ID.
         * @param  init      The initializiation block.
         * @return           The new object.
         */
        public fun make(
                @StringRes messageId: Int,
                init: MessageBuilder.() -> Unit
        ): MessageBuilder = MessageBuilder(messageId).apply(init)
    }

    /**
     * Marks the `MessageBuilder` DSL's objects.
     */
    @DslMarker
    public annotation class Dsl
}

/**
 * Arguments for formatting a string as defined in `Formatter` class.
 *
 * @since 1.0.0
 */
@MessageBuilder.Dsl
public class MessageArguments internal constructor(
        internal var args: MutableList<Any?>?
) {

    /**
     * Adds an argument.
     *
     * @param arg The argument.
     */
    public fun add(arg: Any?) {
        if (args == null) {
            args = mutableListOf(arg)
        } else {
            args?.add(arg)
        }
    }
}