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

import android.content.res.Resources
import android.os.Build
import android.security.keystore.KeyExpiredException
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import io.reactivex.Maybe
import io.reactivex.MaybeEmitter
import io.reactivex.Single
import io.reactivex.SingleEmitter
import it.scoppelletti.spaceship.applicationException
import it.scoppelletti.spaceship.io.IOProvider
import it.scoppelletti.spaceship.io.closeQuietly
import mu.KLogger
import mu.KotlinLogging
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.lang.RuntimeException
import java.security.GeneralSecurityException
import java.security.KeyStore
import java.security.SecureRandom
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Implementation of the `CipherFactory` interface for Marshmallow.
 *
 * @constructor                  Constructor.
 * @param       resources        Resources of this application.
 * @param       ioProvider       Provides I/O components.
 * @param       random           CSRNG.
 * @param       cipherProvider   Provides components for encryption/decryption.
 * @param       cipherIOProvider Provides `Cipher` streams.
 */
@RequiresApi(Build.VERSION_CODES.M)
internal class CipherFactoryMarshmallow(
        private val resources: Resources,
        private val ioProvider: IOProvider,
        private val random: SecureRandom,
        private val cipherProvider: CipherProvider,
        private val cipherIOProvider: CipherIOProvider
) : CipherFactory {

    override fun newEncryptor(alias: String, expire: Int): Single<Cipher> =
            Single.create<SecretKey> { emitter ->
                onNewSecretKeySubscribe(emitter, alias, expire)
            }.flatMap { secretKey ->
                Single.create<ByteArray> { emitter ->
                    onNewIVSubscribe(emitter, alias)
                }.map { iv ->
                    Pair(secretKey, iv)
                }
            }.flatMap { (secretKey, iv) ->
                Single.create<Cipher> { emitter ->
                    onNewEncryptorSubscribe(emitter, secretKey, iv)
                }
            }

    /**
     * Creates a new `Cipher` instance for encryption.
     *
     * @param emitter The observable implementation.
     * @param key     Encryption key.
     * @param iv      `IV`.
     */
    private fun onNewEncryptorSubscribe(
            emitter: SingleEmitter<Cipher>,
            key: SecretKey,
            iv: ByteArray
    ) {
        val cipher: Cipher
        val params: GCMParameterSpec

        try {
            params = GCMParameterSpec(resources.getInteger(
                    R.integer.it_scoppelletti_security_keyLen), iv)
            cipher = cipherProvider.createCipher(resources.getString(
                    R.string.it_scoppelletti_security_aesMode)).apply {
                init(Cipher.ENCRYPT_MODE, key, params)
            }
        } catch (ex: GeneralSecurityException) {
            emitter.tryOnError(ex)
            return
        }

        logger.debug("Encrypting cipher initialized.")
        emitter.onSuccess(cipher)
    }

    override fun newDecryptor(alias: String): Single<Cipher> =
            Maybe.create<SecretKey> { emitter ->
                onLoadSecretKeySubscribe(emitter, alias)
            }.flatMap { secretKey ->
                Maybe.create<ByteArray> { emitter ->
                    onLoadIVSubscribe(emitter, alias)
                }.map { iv ->
                    Pair(secretKey, iv)
                }
            }.flatMap { (secretKey, iv) ->
                Maybe.create<Cipher> { emitter ->
                    onNewDecryptorSubscribe(emitter, secretKey, iv)
                }
            }.switchIfEmpty(
                    Single.error<Cipher>(applicationException {
                        message(R.string.it_scoppelletti_security_err_aliasNotFound) {
                            arguments {
                                add(alias)
                            }
                        }
                    }))

    /**
     * Creates a new `Cipher` instance for decryption.
     *
     * @param emitter The observable implementation.
     * @param key     Decryption key.
     * @param iv      `IV`.
     */
    private fun onNewDecryptorSubscribe(
            emitter: MaybeEmitter<Cipher>,
            key: SecretKey,
            iv: ByteArray
    ) {
        val cipher: Cipher
        val params: GCMParameterSpec

        try {
            params = GCMParameterSpec(resources.getInteger(
                    R.integer.it_scoppelletti_security_keyLen), iv)
            cipher = cipherProvider.createCipher(resources.getString(
                    R.string.it_scoppelletti_security_aesMode)).apply {
                init(Cipher.DECRYPT_MODE, key, params)
            }
        } catch (ex: KeyExpiredException) {
            logger.debug("Secret key is expired.", ex)
            emitter.onComplete()
            return
        } catch (ex: GeneralSecurityException) {
            emitter.tryOnError(ex)
            return
        }

        logger.debug("Decrypting cipher initialized.")
        emitter.onSuccess(cipher)
    }

    /**
     * Generates a new secret key.
     *
     * @param emitter The observable implementation.
     * @param alias   Alias of the key.
     * @param expire  Expiration of the key (days).
     */
    private fun onNewSecretKeySubscribe(
            emitter: SingleEmitter<SecretKey>,
            alias: String,
            expire: Int
    ) {
        val secretKey: SecretKey
        val keyGen: KeyGenerator
        val params: AlgorithmParameterSpec

        params = cipherProvider.createKeyGenParameterSpec(alias, expire)

        if (emitter.isDisposed) {
            return
        }

        try {
            keyGen = cipherProvider.createKeyGenerator(
                    KeyProperties.KEY_ALGORITHM_AES,
                    SecurityExt.KEYSTORE_TYPE)
                    .apply {
                        init(params, random)
                    }
        } catch (ex: GeneralSecurityException) {
            emitter.tryOnError(ex)
            return
        }

        if (emitter.isDisposed) {
            return
        }

        secretKey = keyGen.generateKey()

        logger.debug { "New secret key $alias generated." }
        emitter.onSuccess(secretKey)
    }

    /**
     * Loads a secret key.
     *
     * @param emitter The observable implementation.
     * @param alias   Alias of the key.
     */
    private fun onLoadSecretKeySubscribe(
            emitter: MaybeEmitter<SecretKey>,
            alias: String
    ) {
        val keyStore: KeyStore
        val entry: KeyStore.Entry?

        try {
            keyStore = cipherProvider.createKeyStore(SecurityExt.KEYSTORE_TYPE)

            if (emitter.isDisposed) {
                return
            }

            entry = keyStore.getEntry(alias, null)
            if (entry == null) {
                logger.debug { "Alias $alias not found." }
                emitter.onComplete()
                return
            }

            if (entry !is KeyStore.SecretKeyEntry) {
                logger.warn { "Alias $alias is not a secret key." }
                keyStore.deleteEntry(alias)
                emitter.onComplete()
                return
            }
        } catch (ex: GeneralSecurityException) {
            emitter.tryOnError(ex)
            return
        }

        logger.debug { "Secret key $alias loaded." }
        emitter.onSuccess(entry.secretKey)
    }

    /**
     * Generates a new `IV`.
     *
     * @param emitter The observable implementation.
     * @param alias   Alias of the key.
     */
    private fun onNewIVSubscribe(
            emitter: SingleEmitter<ByteArray>,
            alias: String
    ) {
        val ivLen: Int
        val file: File
        val iv: ByteArray
        var outputStream: OutputStream? = null
        var encoder: OutputStream? = null

        try {
            file = getKeystoreFile(alias)
        } catch (ex: RuntimeException) {
            emitter.tryOnError(ex)
            return
        }

        ivLen = resources.getInteger(R.integer.it_scoppelletti_security_ivLen)
        iv = ByteArray(ivLen)
        random.nextBytes(iv)
        logger.debug("New IV generated.")

        if (emitter.isDisposed) {
            return
        }

        try {
            outputStream = FileOutputStream(file, false)

            encoder = ioProvider.base64OutputStream(outputStream)
            outputStream = null

            encoder.write(iv, 0, ivLen)
            encoder.flush()
        } catch (ex: IOException) {
            iv.fill(0x0)
            logger.error(ex) { "Failed to save IV in file $file." }
            emitter.tryOnError(ex)
            return
        } finally {
            outputStream?.closeQuietly()
            encoder?.closeQuietly()
        }

        logger.debug { "IV stored locally in file $file." }
        emitter.onSuccess(iv)
    }

    /**
     * Loads an `IV`.
     *
     * @param emitter The observable implementation.
     * @param alias   Alias of the key.
     */
    private fun onLoadIVSubscribe(
            emitter: MaybeEmitter<ByteArray>,
            alias: String
    ) {
        val ivLen: Int
        val n: Int
        val file: File
        val iv: ByteArray
        var inputStream: InputStream? = null
        var decoder: InputStream? = null

        try {
            file = getKeystoreFile(alias)
        } catch (ex: RuntimeException) {
            emitter.tryOnError(ex)
            return
        }

        ivLen = resources.getInteger(R.integer.it_scoppelletti_security_ivLen)
        iv = ByteArray(ivLen)

        try {
            inputStream = FileInputStream(file)

            decoder = ioProvider.base64InputStream(inputStream)
            inputStream = null

            n = decoder.read(iv, 0, ivLen)
            if (n != ivLen) {
                throw IOException("IV corrupted.")
            }
        } catch (ex: FileNotFoundException) {
            logger.debug(ex) { "IV not found in file $file." }
            emitter.onComplete()
            return
        } catch (ex: IOException) {
            iv.fill(0x0)
            logger.error(ex) { "Failed to load IV from file $file." }
            emitter.tryOnError(ex)
            return
        } finally {
            inputStream?.closeQuietly()
            decoder?.closeQuietly()
        }

        logger.debug { "IV loaded from file $file." }
        emitter.onSuccess(iv)
    }

    /**
     * Returns the file containing a `IV`.
     *
     * @param  alias Alias of the key.
     * @return       The file path.
     */
    private fun getKeystoreFile(alias: String): File {
        if (alias.indexOfAny(charArrayOf('/', '.'), 0, true) >= 0 ||
                alias.endsWith(CipherFactoryMarshmallow.EXT_KEYSTORE, true)) {
            throw applicationException {
                message(R.string.it_scoppelletti_security_err_aliasInvalid) {
                    arguments {
                        add(alias)
                    }
                }
            }
        }

        return File(ioProvider.noBackupFilesDir, alias +
                CipherFactoryMarshmallow.EXT_KEYSTORE)
    }

    override fun cipherInputStream(
            inputStream: InputStream,
            cipher: Cipher
    ): InputStream = cipherIOProvider.cipherInputStream(inputStream, cipher)

    override fun cipherOutputStream(
            outputStream: OutputStream,
            cipher: Cipher
    ): OutputStream = cipherIOProvider.cipherOutputStream(outputStream, cipher)

    private companion object {
        const val EXT_KEYSTORE: String = ".ks"
        val logger: KLogger = KotlinLogging.logger {}
    }
}
