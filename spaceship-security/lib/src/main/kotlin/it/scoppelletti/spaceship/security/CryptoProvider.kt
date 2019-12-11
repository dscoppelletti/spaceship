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

@file:Suppress("RedundantVisibilityModifier")

package it.scoppelletti.spaceship.security

import android.content.Context
import android.os.Build
import it.scoppelletti.spaceship.io.IOProvider
import it.scoppelletti.spaceship.security.i18n.SecurityMessages
import org.threeten.bp.Clock
import java.io.InputStream
import java.io.OutputStream
import java.security.GeneralSecurityException
import java.security.Key
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey

/**
 * Cryptography service provider.
 *
 * @since 1.0.0
 */
public interface CryptoProvider {

    /**
     * Generates a secret key.
     *
     * @param  alias  Alias of the key.
     * @param  expire Expiration of the key (days). If `<= 0`, the key never
     *                expires.
     * @return        The new key.
     */
    suspend fun newSecretKey(alias: String, expire: Int): SecretKey

    /**
     * Loads a secret key.
     *
     * @param  alias Alias of the key.
     * @return       The new key.
     */
    suspend fun loadSecretKey(alias: String): SecretKey

    /**
     * Creates a new `Cipher` instance for an encryption operation.
     *
     * @param  key A key.
     * @return     The new cipher.
     */
    @Throws(GeneralSecurityException::class)
    suspend fun newEncryptor(key: Key): Cipher

    /**
     * Creates a new `Cipher` instance for an decryption operation.
     *
     * @param  key The key.
     * @param  iv  The `IV`.
     * @return     The new cipher.
     */
    @Throws(GeneralSecurityException::class)
    suspend fun newDecryptor(key: Key, iv: ByteArray): Cipher

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

/**
 * Creates a new `CryptoProvider` instance.
 *
 * @param  context          Context.
 * @param  ioProvider       Provides I/O components.
 * @param  clock            Provides access to the current instant.
 * @param  random           CSRNG.
 * @param  securityMessages String resources.
 * @return                  The new object.
 * @since                   1.0.0
 */
public fun cryptoProvider(
        context: Context,
        ioProvider: IOProvider,
        clock: Clock,
        random: SecureRandom,
        securityMessages: SecurityMessages
): CryptoProvider =
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ->
                CryptoProviderMarshmallow(random,
                        DefaultSecurityBridge(context, clock), securityMessages)
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 ->
                CryptoProviderJellyBeanMR2(ioProvider, clock,
                        random, DefaultSecurityBridge(context, clock),
                        securityMessages)
            else -> DefaultCryptoProvider(random, DefaultSecurityBridge(context,
                    clock), securityMessages)
        }
