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

import android.os.Build
import io.reactivex.Single
import io.reactivex.SingleEmitter
import it.scoppelletti.spaceship.applicationException
import mu.KLogger
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

internal class DefaultCryptoProvider(
        private val random: SecureRandom,
        private val securityBridge: SecurityBridge
) : CryptoProvider {

    override fun newEncryptor(key: Key): Single<Cipher> =
            Single.create<Cipher> { emitter ->
                onNewEncryptorSubscribe(emitter, key)
            }

    /**
     * Creates a new `Cipher` instance for an encryption operation.
     *
     * @param  emitter The observable implementation.
     * @param  key     The key.
     * @return         The new observable object.
     */
    private fun onNewEncryptorSubscribe(
            emitter: SingleEmitter<Cipher>,
            key: Key
    ) {
        val cipher: Cipher
        val params: AlgorithmParameterSpec
        val iv: ByteArray

        try {
            cipher = newCipher().apply {
                init(Cipher.ENCRYPT_MODE, key, random)
            }

            if (cipher.iv != null && !cipher.iv.isEmpty()) {
                logger.debug("Using auto-generated IV.")
                emitter.onSuccess(cipher)
                return
            }

            if (emitter.isDisposed) {
                return
            }

            iv = ByteArray(SecurityExt.IV_SIZE)
            random.nextBytes(iv)
            logger.debug("IV generated.")

            if (emitter.isDisposed) {
                return
            }

            params = securityBridge.createCipherParameterSpec(iv)
            cipher.init(Cipher.ENCRYPT_MODE, key, params, random)
        } catch (ex: GeneralSecurityException) {
            emitter.tryOnError(ex)
            return
        }

        emitter.onSuccess(cipher)
    }

    override fun newDecryptor(key: Key, iv: ByteArray): Single<Cipher> =
            Single.create<Cipher> { emitter ->
                onNewDecryptorSubscribe(emitter, key, iv)
            }

    /**
     * Creates a new `Cipher` instance for an decryption operation.
     *
     * @param  emitter The observable implementation.
     * @param  key     The key.
     * @param  iv      The `IV`.
     * @return         The new observable object.
     */
    private fun onNewDecryptorSubscribe(
            emitter: SingleEmitter<Cipher>,
            key: Key,
            iv: ByteArray
    ) {
        val cipher: Cipher
        val params: AlgorithmParameterSpec

        try {
            params = securityBridge.createCipherParameterSpec(iv)

            cipher = newCipher().apply {
                init(Cipher.DECRYPT_MODE, key, params, random)
            }
        } catch (ex: GeneralSecurityException) {
            emitter.tryOnError(ex)
            return
        }

        emitter.onSuccess(cipher)
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

    override fun newSecretKey(alias: String, expire: Int): Single<SecretKey> =
            Single.error(noSuchProviderException())

    override fun loadSecretKey(alias: String): Single<SecretKey> =
            Single.error(noSuchProviderException())

    private fun noSuchProviderException() =
            applicationException {
                message(R.string.it_scoppelletti_security_err_providerNotFound) {
                    arguments {
                        add(SecurityExt.PROVIDER_ANDROID)
                    }
                }
                cause = NoSuchProviderException(SecurityExt.PROVIDER_ANDROID)
            }

    private companion object {
        val logger: KLogger = KotlinLogging.logger {}
    }
}
