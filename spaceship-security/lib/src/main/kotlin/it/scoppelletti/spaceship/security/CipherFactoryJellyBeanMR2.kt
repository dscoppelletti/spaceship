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
import androidx.annotation.RequiresApi
import io.reactivex.Maybe
import io.reactivex.MaybeEmitter
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.internal.operators.maybe.MaybeCreate
import it.scoppelletti.spaceship.applicationException
import it.scoppelletti.spaceship.io.IOProvider
import it.scoppelletti.spaceship.io.closeQuietly
import it.scoppelletti.spaceship.types.TimeProvider
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
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
import java.security.cert.Certificate
import java.security.cert.X509Certificate
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

/**
 * Implementation of the `CipherFactory` interface for JellyBean (MR2).
 *
 * @constructor                  Constructor.
 * @param       resources        Resources of this application.
 * @param       ioProvider       Provides I/O components.
 * @param       timeProvider     Provides components for operations on dates and
 *                               times.
 * @param       random           CSRNG.
 * @param       cipherProvider   Provides components for encryption/decryption.
 * @param       cipherIOProvider Provides `Cipher` streams.
 */
@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
internal class CipherFactoryJellyBeanMR2(
        private val resources: Resources,
        private val ioProvider: IOProvider,
        private val timeProvider: TimeProvider,
        private val random: SecureRandom,
        private val cipherProvider: CipherProvider,
        private val cipherIOProvider: CipherIOProvider
) : CipherFactory {

    override fun newEncryptor(alias: String, expire: Int): Single<Cipher> =
            Single.create<PublicKey> { emitter ->
                onNewKeyPairSubscribe(emitter, alias, expire)
            }.flatMap { publicKey ->
                Single.create<SecretKey> { emitter ->
                    onNewSecretKeySubscribe(emitter, publicKey, alias)
                }
            }.flatMap { secretKey ->
                Single.create<Cipher> { emitter ->
                    onNewEncryptorSubscribe(emitter, secretKey)
                }
            }

    /**
     * Creates a new `Cipher` instance for encryption.
     *
     * @param emitter The observable implementation.
     * @param key     Encryption key.
     */
    private fun onNewEncryptorSubscribe(
            emitter: SingleEmitter<Cipher>,
            key: SecretKey
    ) {
        val cipher: Cipher

        try {
            cipher = cipherProvider.createCipher(resources.getString(
                    R.string.it_scoppelletti_security_aesMode),
                    CipherFactoryJellyBeanMR2.PROVIDER_BC).apply {
                init(Cipher.ENCRYPT_MODE, key)
            }
        } catch (ex: GeneralSecurityException) {
            emitter.tryOnError(ex)
            return
        }

        logger.debug("Encrypting cipher initialized.")
        emitter.onSuccess(cipher)
    }

    override fun newDecryptor(alias: String): Single<Cipher> =
            Maybe.create<PrivateKey> { emitter ->
                onLoadKeyPairSubscribe(emitter, alias)
            }.flatMap { privateKey ->
                Maybe.create<SecretKey> { emitter ->
                    onLoadSecretKeySubscribe(emitter, privateKey, alias)
                }
            }.flatMap { secretKey ->
                MaybeCreate<Cipher> { emitter ->
                    onNewDecryptorSubscribe(emitter, secretKey)
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
     */
    private fun onNewDecryptorSubscribe(
            emitter: MaybeEmitter<Cipher>,
            key: SecretKey
    ) {
        val cipher: Cipher

        try {
            cipher = cipherProvider.createCipher(resources.getString(
                    R.string.it_scoppelletti_security_aesMode),
                    CipherFactoryJellyBeanMR2.PROVIDER_BC).apply {
                init(Cipher.DECRYPT_MODE, key)
            }
        } catch (ex: GeneralSecurityException) {
            emitter.tryOnError(ex)
            return
        }

        logger.debug("Decrypting cipher initialized.")
        emitter.onSuccess(cipher)
    }

    /**
     * Generates a new key pair.
     *
     * @param emitter The observable implementation.
     * @param alias   Alias of the key.
     * @param expire  Expiration of the key (days).
     */
    private fun onNewKeyPairSubscribe(
            emitter: SingleEmitter<PublicKey>,
            alias: String,
            expire: Int
    ) {
        val keyPair: KeyPair
        val keyGen: KeyPairGenerator
        val params: AlgorithmParameterSpec

        params = cipherProvider.createKeyPairGenParameterSpec(alias, expire)

        if (emitter.isDisposed) {
            return
        }

        try {
            keyGen = cipherProvider.createKeyPairGenerator(
                    CipherFactoryJellyBeanMR2.KEY_ALGORITHM_RSA,
                    SecurityExt.KEYSTORE_TYPE)
                    .apply {
                        initialize(params, random)
                    }
        } catch (ex: GeneralSecurityException) {
            emitter.tryOnError(ex)
            return
        }

        keyPair = keyGen.generateKeyPair()
        logger.debug { "New key pair $alias generated." }
        return emitter.onSuccess(keyPair.public)
    }

    /**
     * Loads the key pair.
     *
     * @param emitter The observable implementation.
     * @param alias   Alias of the key.
     */
    private fun onLoadKeyPairSubscribe(
            emitter: MaybeEmitter<PrivateKey>,
            alias: String
    ) {
        val keyStore: KeyStore
        val cert: Certificate?
        val entry: KeyStore.Entry?

        try {
            keyStore = cipherProvider.createKeyStore(SecurityExt.KEYSTORE_TYPE)

            if (emitter.isDisposed) {
                return
            }

            cert = keyStore.getCertificate(alias)
            if (cert == null) {
                logger.debug { "Certificate $alias not found." }
                emitter.onComplete()
                return
            }

            if (cert !is X509Certificate) {
                logger.warn { "Alias $alias is not a certificate." }
                emitter.onComplete()
                return
            }

            // It seems that the expiration of the certificate is ignored
            cert.checkValidity(timeProvider.currentTime().time)

            if (emitter.isDisposed) {
                return
            }

            entry = keyStore.getEntry(alias, null)
            if (entry == null) {
                logger.debug { "Alias $alias not found." }
                emitter.onComplete()
                return
            }

            if (entry !is KeyStore.PrivateKeyEntry) {
                logger.warn { "Alias $alias is not a private key." }
                emitter.onComplete()
                return
            }
        } catch (ex: Exception) { // GeneralSecurityException|IOException
            emitter.tryOnError(ex)
            return
        }

        if (emitter.isDisposed) {
            return
        }

        logger.debug { "Key pair $alias loaded." }
        emitter.onSuccess(entry.privateKey)
    }

    /**
     * Generates a new secret key.
     *
     * @param emitter   The observable implementation.
     * @param publicKey Encryption key.
     * @param alias     Alias of the secret key.
     */
    private fun onNewSecretKeySubscribe(
            emitter: SingleEmitter<SecretKey>,
            publicKey: PublicKey,
            alias: String
    ) {
        val keyLen: Int
        val file: File
        val secretKey: SecretKey
        val cipher: Cipher
        var key: ByteArray? = null
        var outputStream: OutputStream? = null
        var encoder: OutputStream? = null
        var encryptor: OutputStream? = null

        try {
            file = getKeystoreFile(alias)
        } catch (ex: RuntimeException) {
            emitter.tryOnError(ex)
            return
        }

        try {
            cipher = cipherProvider.createCipher(resources.getString(
                    R.string.it_scoppelletti_security_rsaMode),
                    CipherFactoryJellyBeanMR2.PROVIDER_ANDROID)
                    .apply {
                        init(Cipher.ENCRYPT_MODE, publicKey)
                    }

            if (emitter.isDisposed) {
                return
            }

            outputStream = FileOutputStream(file, false)

            encoder = ioProvider.base64OutputStream(outputStream)
            outputStream = null

            encryptor = cipherOutputStream(encoder, cipher)
            encoder = null

            keyLen = resources.getInteger(
                    R.integer.it_scoppelletti_security_keyLen)
            key = ByteArray(keyLen)
            random.nextBytes(key)
            logger.debug("New secret key generated.")

            if (emitter.isDisposed) {
                return
            }

            encryptor.write(key, 0, keyLen)
            encryptor.flush()

            if (emitter.isDisposed) {
                return
            }

            secretKey = SecretKeySpec(key,
                    CipherFactoryJellyBeanMR2.KEY_ALGORITHM_AES)
        } catch (ex: Exception) { // GeneralSecurityException|IOException
            logger.error(ex) { "Failed to save secret key in file $file."}
            emitter.tryOnError(ex)
            return
        } finally {
            outputStream?.closeQuietly()
            encoder?.closeQuietly()
            encryptor?.closeQuietly()
            key?.fill(0x0)
        }

        logger.debug { "Secret key encrypted in file $file." }
        emitter.onSuccess(secretKey)
    }

    /**
     * Loads the secret key.
     *
     * @param emitter    The observable implementation.
     * @param privateKey Decryption key.
     * @param alias      Alias of the secret key.
     */
    private fun onLoadSecretKeySubscribe(
            emitter: MaybeEmitter<SecretKey>,
            privateKey: PrivateKey,
            alias: String
    ) {
        val n: Int
        val keyLen: Int
        val file: File
        val secretKey: SecretKey
        val cipher: Cipher
        var key: ByteArray? = null
        var inputStream: InputStream? = null
        var decoder: InputStream? = null
        var decryptor: InputStream? = null

        try {
            file = getKeystoreFile(alias)
        } catch (ex: RuntimeException) {
            emitter.tryOnError(ex)
            return
        }

        try {
            cipher = cipherProvider.createCipher(resources.getString(
                    R.string.it_scoppelletti_security_rsaMode),
                    CipherFactoryJellyBeanMR2.PROVIDER_ANDROID)
                    .apply {
                        init(Cipher.DECRYPT_MODE, privateKey)
                    }

            if (emitter.isDisposed) {
                return
            }

            inputStream = FileInputStream(file)

            decoder = ioProvider.base64InputStream(inputStream)
            inputStream = null

            decryptor = cipherInputStream(decoder, cipher)
            decoder = null

            keyLen = resources.getInteger(
                    R.integer.it_scoppelletti_security_keyLen)
            key = ByteArray(keyLen)
            n = decryptor.read(key, 0, keyLen)
            if (n != keyLen) {
                throw IOException("Corrupted secret key in file $file.")
            }

            if (emitter.isDisposed) {
                return
            }

            secretKey = SecretKeySpec(key,
                    CipherFactoryJellyBeanMR2.KEY_ALGORITHM_AES)
        } catch (ex: FileNotFoundException) {
            logger.debug(ex) { "Secret key not found in file $file." }
            emitter.onComplete()
            return
        } catch (ex: Exception) { // GeneralSecurityException|IOException
            logger.error(ex) { "Failed to load secret key from file $file." }
            emitter.tryOnError(ex)
            return
        } finally {
            inputStream?.closeQuietly()
            decoder?.closeQuietly()
            decryptor?.closeQuietly()
            key?.fill(0x0)
        }

        logger.debug { "Secret key decrypted from file $file." }
        emitter.onSuccess(secretKey)
    }

    /**
     * Returns the file containing a secret key.
     *
     * @param  alias Alias of the key.
     * @return       The file path.
     */
    private fun getKeystoreFile(alias: String): File {
        if (alias.indexOfAny(charArrayOf('/', '.'), 0, true) >= 0 ||
                alias.endsWith(CipherFactoryJellyBeanMR2.EXT_KEYSTORE, true)) {
            throw applicationException {
                message(R.string.it_scoppelletti_security_err_aliasInvalid) {
                    arguments {
                        add(alias)
                    }
                }
            }
        }

        return File(ioProvider.noBackupFilesDir, alias +
                CipherFactoryJellyBeanMR2.EXT_KEYSTORE)
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
        const val KEY_ALGORITHM_AES: String = "AES"
        const val KEY_ALGORITHM_RSA: String = "RSA"
        const val PROVIDER_ANDROID: String = "AndroidOpenSSL"
        const val PROVIDER_BC: String = "BC"
        val logger: KLogger = KotlinLogging.logger {}
    }
}
