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

@file:Suppress("JoinDeclarationAndAssignment")

package it.scoppelletti.spaceship.security

import android.os.Build
import it.scoppelletti.spaceship.ApplicationException
import it.scoppelletti.spaceship.security.i18n.SecurityMessages
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import java.io.InputStream
import java.io.OutputStream
import java.security.GeneralSecurityException
import java.security.Key
import java.security.NoSuchProviderException
import java.security.SecureRandom
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.SecretKey

/**
 * Supports the implementation of the `CryptoProvider` interface.
 */
internal class DefaultCryptoProvider(
        private val random: SecureRandom,
        private val securityBridge: SecurityBridge,
        private val securityMessages: SecurityMessages
) : CryptoProvider {

    override suspend fun newSecretKey(alias: String, expire: Int): SecretKey {
        throw noSuchProviderException()
    }

    override suspend fun loadSecretKey(alias: String): SecretKey {
        throw noSuchProviderException()
    }

    @Throws(GeneralSecurityException::class)
    override suspend fun newEncryptor(key: Key): Cipher =
            withContext(Dispatchers.Default) {
                val cipher: Cipher
                val params: AlgorithmParameterSpec
                val iv: ByteArray

                cipher = newCipher().apply {
                    init(Cipher.ENCRYPT_MODE, key, random)
                }

                if (cipher.iv != null && cipher.iv.isNotEmpty()) {
                    logger.debug("Using auto-generated IV.")
                    return@withContext cipher
                }

                if (!isActive) {
                    throw CancellationException()
                }

                iv = ByteArray(SecurityExt.IV_SIZE)
                random.nextBytes(iv)
                logger.debug("IV generated.")

                if (!isActive) {
                    throw CancellationException()
                }

                params = securityBridge.createCipherParameterSpec(iv)
                cipher.apply {
                    init(Cipher.ENCRYPT_MODE, key, params, random)
                }
            }

    @Throws(GeneralSecurityException::class)
    override suspend fun newDecryptor(key: Key, iv: ByteArray): Cipher =
            withContext(Dispatchers.Default) {
                val params: AlgorithmParameterSpec

                params = securityBridge.createCipherParameterSpec(iv)

                newCipher().apply {
                    init(Cipher.DECRYPT_MODE, key, params, random)
                }
            }

    /**
     * Creates a new `Cipher` instance.
     *
     * @return The new object.
     */
    private fun newCipher(): Cipher =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                securityBridge.createCipher(SecurityExt.TRANSFORMATION_AES)
            else securityBridge.createCipher(SecurityExt.TRANSFORMATION_AES,
                    SecurityExt.PROVIDER_BC)

    override fun cipherInputStream(
            inputStream: InputStream,
            cipher: Cipher
    ): InputStream =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N &&
                    Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1)
                // - API 24, 25
                // If encrypted data are corrupted, the AEADBadTagException
                // exception is swallowed by the decrypting OpenJDK7
                // CipherInputStream and the result is an empty input stream:
                // I need use a newer version of the CipherInputStream class.
                CipherInputStreamAndroid(inputStream, cipher)
            else
                CipherInputStream(inputStream, cipher)

    override fun cipherOutputStream(
            outputStream: OutputStream,
            cipher: Cipher
    ): OutputStream = CipherOutputStream(outputStream, cipher)

    private fun noSuchProviderException() =
            ApplicationException(securityMessages.errorProviderNotFound(
                    SecurityExt.PROVIDER_ANDROID),
                    NoSuchProviderException(SecurityExt.PROVIDER_ANDROID))

    private companion object {
        val logger = KotlinLogging.logger {}
    }
}
