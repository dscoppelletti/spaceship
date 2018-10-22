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

package it.scoppelletti.spaceship.security

import java.io.InputStream
import java.io.OutputStream
import javax.crypto.Cipher

/**
 * Provides `Cipher` streams.
 */
internal interface CipherIOProvider {

    /**
     * Returns an input stream that ciphers the data read in from the underlying
     * input stream.
     *
     * @param  inputStream Original input stream.
     * @param  cipher      A `Cipher` object.
     * @return             Cipher stream.
     */
    fun cipherInputStream(inputStream: InputStream, cipher: Cipher): InputStream

    /**
     * Returns an output stream that ciphers the data before writing them out to
     * the underlying output stream.
     *
     * @param  outputStream Original output stream.
     * @param  cipher       A `Cipher` object.
     * @return              Cipher stream.
     */
    fun cipherOutputStream(
            outputStream: OutputStream,
            cipher: Cipher
    ): OutputStream
}
