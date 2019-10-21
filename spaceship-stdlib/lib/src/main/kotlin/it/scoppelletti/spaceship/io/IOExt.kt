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

@file:Suppress("RedundantExplicitType", "RedundantVisibilityModifier", "unused")

package it.scoppelletti.spaceship.io

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import java.io.Closeable
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

private val logger = KotlinLogging.logger { }

/**
 * I/O extensions.
 */
public object IOExt {

    /**
     * Separator between the base name and the extension in a file name.
     */
    public const val EXT_SEPARATOR = '.'
}

/**
 * Closes a stream ignoring any exceptions.
 *
 * The returned value is always `null` and can be assigned to the receiver, so
 * that it can be used to check whether the stream is still opened
 * (`stream != null`) or has been closed (`stream == null`).
 *
 * @receiver Stream.
 * @return   `null`
 * @since    1.0.0
 */
public fun <T : Closeable> T.closeQuietly(): T? {
    try {
        this.close()
    } catch (ex: IOException) {
        logger.error("Failed to close stream.", ex)
    }

    return null
}

/**
 * Copy a stream to another stream.
 *
 * @param inStream  Input stream.
 * @param outStream Output stream.
 * @param bufSize   Size of the buffer used for copying.
 * @since           1.0.0
 */
@Throws(IOException::class)
public suspend fun copy(
        inStream: InputStream,
        outStream: OutputStream,
        bufSize: Int = DEFAULT_BUFFER_SIZE
) = withContext(Dispatchers.IO) {
    var n: Int
    val buf: ByteArray = ByteArray(bufSize)

    n = inStream.read(buf, 0, bufSize)
    while (n > 0) {
        if (!this.isActive) {
            return@withContext
        }

        outStream.write(buf, 0, n)
        if (!this.isActive) {
            return@withContext
        }

        n = inStream.read(buf, 0, bufSize)
    }

    outStream.flush()
}
