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

package it.scoppelletti.spaceship.io

import android.support.annotation.WorkerThread
import io.reactivex.CompletableEmitter
import io.reactivex.CompletableOnSubscribe
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/**
 * Implementation of a `Completable` for copying a stream to another stream.
 *
 * @since 1.0.0
 *
 * @constructor           Costructor.
 * @param       inStream  The input stream.
 * @param       outStream The output stream.
 * @param       bufSize   Size of the buffer used for copying.
 */
@WorkerThread
public class FileCopier(
        private val inStream: InputStream,
        private val outStream: OutputStream,
        private val bufSize: Int = DEFAULT_BUFFER_SIZE
) : CompletableOnSubscribe {

    override fun subscribe(emitter: CompletableEmitter) {
        var n: Int
        val buf: ByteArray = ByteArray(bufSize)

        try {
            n = inStream.read(buf, 0, bufSize)
            while (n > 0) {
                if (emitter.isDisposed) {
                    return
                }

                outStream.write(buf, 0, n)
                if (emitter.isDisposed) {
                    return
                }

                n = inStream.read(buf, 0, bufSize)
            }

            outStream.flush()
        } catch (ex: IOException) {
            emitter.tryOnError(ex)
        }

        emitter.onComplete()
    }
}