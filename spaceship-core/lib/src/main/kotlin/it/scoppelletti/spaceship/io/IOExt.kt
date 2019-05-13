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

package it.scoppelletti.spaceship.io

import mu.KotlinLogging
import java.io.Closeable
import java.io.IOException

private val logger = KotlinLogging.logger {}

/**
 * Closes a stream ignoring any exceptions.
 *
 * @receiver Stream.
 * @since    1.0.0
 */
public fun Closeable.closeQuietly() {
    try {
        this.close()
    } catch (ex: IOException) {
        logger.error("Failed to close stream.", ex)
    }
}
