/*
 * Copyright (C) 2017 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

import io.reactivex.Single
import java.io.InputStream
import java.io.OutputStream
import java.security.KeyStoreException
import javax.crypto.Cipher

/**
 * Stub implementation of the `CipherFactory` interface.
 *
 * @constructor                  Constructor.
 * @param       cipherIOProvider Provides `Cipher` streams.
 */
internal class CipherFactoryStub(
        private val cipherIOProvider: CipherIOProvider
) : CipherFactory {

    override fun newEncryptor(alias: String, expire: Int): Single<Cipher> =
            Single.error(KeyStoreException("AndroidKeyStore not implemented."))

    override fun newDecryptor(alias: String): Single<Cipher> =
            Single.error(KeyStoreException("AndroidKeyStore not implemented."))

    override fun cipherInputStream(
            inputStream: InputStream,
            cipher: Cipher
    ): InputStream = cipherIOProvider.cipherInputStream(inputStream, cipher)

    override fun cipherOutputStream(
            outputStream: OutputStream,
            cipher: Cipher
    ): OutputStream = cipherIOProvider.cipherOutputStream(outputStream, cipher)
}
