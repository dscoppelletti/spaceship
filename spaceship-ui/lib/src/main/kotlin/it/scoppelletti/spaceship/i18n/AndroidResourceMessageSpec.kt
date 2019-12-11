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

@file:Suppress("JoinDeclarationAndAssignment", "RedundantVisibilityModifier")

package it.scoppelletti.spaceship.i18n

import android.content.res.Resources
import androidx.annotation.StringRes
import it.scoppelletti.spaceship.types.joinLines
import mu.KotlinLogging

/**
 * Message specification that can be resolved by an Android string resource.
 *
 * @since 1.0.0
 *
 * @property stringId The string resource ID.
 * @property args     Arguments for the message. The string resource must be a
 *                    printf-style format string as defined by the JDK class
 *                    `Formatter`.
 */
public data class AndroidResourceMessageSpec(
        private val resources: Resources,

        @StringRes
        public val stringId: Int,

        public val args: Array<Any?> = arrayOf()
) : MessageSpec {

    override fun hashCode(): Int {
        var value = 17

        value += value * 37 + stringId
        value += value * 37 + args.contentDeepHashCode()

        return value
    }

    override fun equals(other: Any?): Boolean =
            other is AndroidResourceMessageSpec &&
                    stringId == other.stringId &&
                    args contentDeepEquals other.args

    override fun toString(): String {
        val resName: String

        resName = try {
            resources.getResourceName(stringId)
        } catch (ex: Resources.NotFoundException) {
            logger.error(ex) { "Resource $stringId not found." }
            stringId.toString()
        }

        return """AndroidResourceMessageSpec($resName,
            |args=${args.contentDeepToString()})""".trimMargin().joinLines()
    }

    private companion object {
        val logger = KotlinLogging.logger { }
    }
}
