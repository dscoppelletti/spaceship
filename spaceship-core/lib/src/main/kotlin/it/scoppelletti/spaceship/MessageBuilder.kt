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
import androidx.annotation.StringRes
import it.scoppelletti.spaceship.types.StringExt
import it.scoppelletti.spaceship.types.trimRaw

/**
 * Message builder.
 *
 * @since 1.0.0
 *
 * @property messageId Message as a string resource ID.
 * @property message   Message as a string.
 * @property arguments Format arguments as defined in the `Formatter` class.
 * @property isSimple  Indicates whether the [messageId] is the only one data
 *                     needed to retrieve the message string or not.
 */
@MessageBuilder.Dsl
public class MessageBuilder private constructor(
        public val messageId: Int,
        public val message: String
) {

    private var args: MutableList<Any?>? = null

    public val arguments: List<Any?>?
        get() = args

    public val isSimple: Boolean
        get() = (messageId > 0 && args == null)

    /**
     * Adds format arguments as defined in the `Formatter` class.
     *
     * @param  init Initializiation block.
     * @return      Collection of the arguments.
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
     * @param  resources Resources of this application.
     * @return           The message string.
     */
    public fun build(resources: Resources): String {
        val v: Array<Any?>

        if (args == null) {
            if (messageId > 0) {
                return resources.getString(messageId)
            } else {
                return message
            }
        }

        v = args?.toTypedArray() ?: emptyArray()
        if (messageId > 0) {
            return resources.getString(messageId, *v)
        }

        return message.format(*v)
    }

    override fun toString(): String = """MessageBuilder(messageId=$messageId,
        |message=$message,args=${args?.joinToString()})""".trimRaw()

    public companion object {

        /**
         * Creates a new `MessageBuilder` instance.
         *
         * @param  messageId Message as a string resource ID.
         * @param  init      Initializiation block.
         * @return           The new object.
         */
        public fun make(
                @StringRes messageId: Int,
                init: MessageBuilder.() -> Unit
        ): MessageBuilder = MessageBuilder(messageId, StringExt.EMPTY)
                .apply(init)

        /**
         * Creates a new `MessageBuilder` instance.
         *
         * @param  message Message.
         * @param  init    Initializiation block.
         * @return         The new object.
         */
        public fun make(
                message: String,
                init: MessageBuilder.() -> Unit
        ): MessageBuilder = MessageBuilder(0, message)
                .apply(init)
    }

    /**
     * Marks the `MessageBuilder` DSL's objects.
     *
     * @since 1.0.0
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
     * @param arg An argument.
     */
    public fun add(arg: Any?) {
        if (args == null) {
            args = mutableListOf(arg)
        } else {
            args?.add(arg)
        }
    }
}