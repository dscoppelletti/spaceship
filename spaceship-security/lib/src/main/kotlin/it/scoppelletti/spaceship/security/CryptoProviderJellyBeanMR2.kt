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
import androidx.annotation.RequiresApi
import io.reactivex.Single
import io.reactivex.SingleEmitter
import it.scoppelletti.spaceship.ApplicationException
import it.scoppelletti.spaceship.applicationException
import it.scoppelletti.spaceship.io.IOProvider
import it.scoppelletti.spaceship.io.closeQuietly
import it.scoppelletti.spaceship.types.TimeProvider
import it.scoppelletti.spaceship.types.trimRaw
import mu.KLogger
import mu.KotlinLogging
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.security.GeneralSecurityException
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.Certificate
import java.security.cert.X509Certificate
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

/**
 * Implementation of the `CryptoProvider` interface for JellyBean (MR2).
 *
 * @constructor                Constructor.
 * @param       ioProvider     Provides I/O components.
 * @param       timeProvider   Provides components for operations on dates and
 *                             times.
 * @param       random         CSRNG.
 * @param       securityBridge Factory methods for security components.
 */
@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
internal class CryptoProviderJellyBeanMR2(
        private val ioProvider: IOProvider,
        private val timeProvider: TimeProvider,
        private val random: SecureRandom,
        private val securityBridge: SecurityBridge
) : CryptoProvider by DefaultCryptoProvider(random, securityBridge) {

    override fun newSecretKey(
            alias: String,
            expire: Int
    ): Single<SecretKey> =
            Single.create<KeyPair> { emitter ->
                onNewKeyPairSubscribe(emitter, alias, expire)
            }.flatMap { keyPair ->
                Single.create<SecretKey> { emitter ->
                    onNewSecretKeySubscribe(emitter, alias, keyPair)
                }
            }

    override fun loadSecretKey(alias: String): Single<SecretKey> =
            Single.create<KeyPair> { emitter ->
                onLoadKeyPairSubscribe(emitter, alias)
            }.flatMap { keyPair ->
                Single.create<SecretKey> { emitter ->
                    onLoadSecretKeySubscribe(emitter, alias, keyPair)
                }
            }

    /**
     * Generates a new key pair.
     *
     * @param emitter The observable implementation.
     * @param alias   Alias of the key.
     * @param expire  Expiration of the key (days).
     */
    private fun onNewKeyPairSubscribe(
            emitter: SingleEmitter<KeyPair>,
            alias: String,
            expire: Int
    ) {
        val keyPair: KeyPair
        val keyGen: KeyPairGenerator
        val params: AlgorithmParameterSpec

        params = securityBridge.createKeyPairGenParameterSpec(alias, expire)

        if (emitter.isDisposed) {
            return
        }

        try {
            keyGen = securityBridge.createKeyPairGenerator(
                    SecurityExt.KEY_ALGORITHM_RSA, SecurityExt.PROVIDER_ANDROID)
                    .apply {
                        initialize(params, random)
                    }
        } catch (ex: GeneralSecurityException) {
            emitter.tryOnError(ex)
            return
        }

        keyPair = keyGen.generateKeyPair()
        logger.debug { "New key pair $alias generated." }

        return emitter.onSuccess(keyPair)
    }

    /**
     * Loads a key pair.
     *
     * @param emitter The observable implementation.
     * @param alias   Alias of the key.
     */
    private fun onLoadKeyPairSubscribe(
            emitter: SingleEmitter<KeyPair>,
            alias: String
    ) {
        val keyStore: KeyStore
        val cert: Certificate?
        val entry: KeyStore.Entry?

        try {
            keyStore = securityBridge.createKeyStore(SecurityExt.KEYSTORE_TYPE)

            if (emitter.isDisposed) {
                return
            }

            cert = keyStore.getCertificate(alias)
            if (cert == null) {
                throw applicationException {
                    message(R.string.it_scoppelletti_security_err_certificateNotFound) {
                        arguments {
                            add(alias)
                        }
                    }
                }
            }

            if (cert !is X509Certificate) {
                throw applicationException {
                    message(R.string.it_scoppelletti_security_err_aliasNotCertificate) {
                        arguments {
                            add(alias)
                        }
                    }
                }
            }

            logger.debug { """Found certificate $alias valid from
${cert.notBefore} to ${cert.notAfter}.""".trimRaw() }

            // If the certificate is expired, the method checkValidity issues
            // a CertificateExpiredException.
            // It seems that the expiration of the certificate is ignored by
            // any further use of the public or private key.
            cert.checkValidity(timeProvider.currentTime().time)

            if (emitter.isDisposed) {
                return
            }

            entry = keyStore.getEntry(alias, null)
            if (entry == null) {
                throw applicationException {
                    message(R.string.it_scoppelletti_security_err_aliasNotFound) {
                        arguments {
                            add(alias)
                        }
                    }
                }
            }

            if (entry !is KeyStore.PrivateKeyEntry) {
                throw applicationException {
                    message(R.string.it_scoppelletti_security_err_aliasNotPrivateKey) {
                        arguments {
                            add(alias)
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            // ApplicationException|GeneralSecurityException
            emitter.tryOnError(ex)
            return
        }

        if (emitter.isDisposed) {
            return
        }

        logger.debug { "Key pair $alias loaded." }
        emitter.onSuccess(KeyPair(cert.publicKey, entry.privateKey))
    }

    /**
     * Generates a new secret key.
     *
     * @param emitter The observable implementation.
     * @param alias   Alias of the key.
     * @param keyPair Key pair.
     */
    private fun onNewSecretKeySubscribe(
            emitter: SingleEmitter<SecretKey>,
            alias: String,
            keyPair: KeyPair
    ) {
        val file: File
        val secretKey: SecretKey
        val keyGen: KeyGenerator
        val cipher: Cipher
        val err: ApplicationException
        var key: ByteArray? = null
        var outputStream: OutputStream? = null
        var encoder: OutputStream? = null

        try {
            file = getKeystoreFile(alias)
        } catch (ex: RuntimeException) {
            emitter.tryOnError(ex)
            return
        }

        try {
            keyGen = securityBridge.createKeyGenerator(
                    SecurityExt.KEY_ALGORITHM_AES, SecurityExt.PROVIDER_BC)
                    .apply {
                        init(SecurityExt.AES_KEYSIZE, random)
                    }

            if (emitter.isDisposed) {
                return
            }

            secretKey = keyGen.generateKey()
            logger.debug("New secret key generated.")

            if (emitter.isDisposed) {
                return
            }

            cipher = securityBridge.createCipher(SecurityExt.TRANSFORMATION_RSA,
                    SecurityExt.PROVIDER_OPENSSL)
                    .apply {
                        init(Cipher.WRAP_MODE, keyPair.public)
                    }

            if (emitter.isDisposed) {
                return
            }

            key = cipher.wrap(secretKey)

            if (emitter.isDisposed) {
                return
            }

            outputStream = FileOutputStream(file, false)

            encoder = ioProvider.base64OutputStream(outputStream)
            outputStream = null

            encoder.write(key, 0, key.size)
            encoder.flush()
        } catch (ex: Exception) {
            // GeneralSecurityException|IllegalStateException|IOException
            err = applicationException {
                message(R.string.it_scoppelletti_security_err_saveSecretKey) {
                    arguments {
                        add(file)
                    }
                }
                cause = ex
            }

            emitter.tryOnError(err)
            return
        } finally {
            outputStream?.closeQuietly()
            encoder?.closeQuietly()
            key?.fill(0x0)
        }

        logger.debug { "Secret key wrapped in file $file." }
        emitter.onSuccess(secretKey)
    }

    /**
     * Loads a secret key.
     *
     * @param emitter The observable implementation.
     * @param alias   Alias of the key.
     * @param keyPair Key pair.
     */
    private fun onLoadSecretKeySubscribe(
            emitter: SingleEmitter<SecretKey>,
            alias: String,
            keyPair: KeyPair
    ) {
        val file: File
        val secretKey: SecretKey
        val cipher: Cipher
        val err: ApplicationException
        var n: Int
        var buf: ByteArray? = null
        var inputStream: InputStream? = null
        var decoder: InputStream? = null
        var outputStream: ByteArrayOutputStream? = null

        try {
            file = getKeystoreFile(alias)
        } catch (ex: RuntimeException) {
            emitter.tryOnError(ex)
            return
        }

        try {
            cipher = securityBridge.createCipher(SecurityExt.TRANSFORMATION_RSA,
                    SecurityExt.PROVIDER_OPENSSL)
                    .apply {
                        init(Cipher.UNWRAP_MODE, keyPair.private)
                    }

            if (emitter.isDisposed) {
                return
            }

            inputStream = FileInputStream(file)

            decoder = ioProvider.base64InputStream(inputStream)
            inputStream = null

            outputStream = ByteArrayOutputStream()

            buf = ByteArray(DEFAULT_BUFFER_SIZE)
            n = decoder.read(buf, 0, DEFAULT_BUFFER_SIZE)
            while (n > 0) {
                if (emitter.isDisposed) {
                    return
                }

                outputStream.write(buf, 0, n)
                n = decoder.read(buf, 0, DEFAULT_BUFFER_SIZE)
            }

            if (emitter.isDisposed) {
                return
            }

            outputStream.flush()
            buf.fill(0x0)
            buf = outputStream.toByteArray()

            if (emitter.isDisposed) {
                return
            }

            secretKey = cipher.unwrap(buf, SecurityExt.KEY_ALGORITHM_AES,
                    Cipher.SECRET_KEY) as SecretKey
        } catch (ex: FileNotFoundException) {
            err = applicationException {
                message(R.string.it_scoppelletti_security_err_secretKeyNotFound) {
                    arguments {
                        add(file)
                    }
                }
                cause = ex
            }

            emitter.tryOnError(err)
            return
        } catch (ex: Exception) {
            // GeneralSecurityException|IllegalStateException|IOException
            err = applicationException {
                message(R.string.it_scoppelletti_security_err_loadSecretKey) {
                    arguments {
                        add(file)
                    }
                }
                cause = ex
            }

            emitter.tryOnError(err)
            return
        } finally {
            inputStream?.closeQuietly()
            decoder?.closeQuietly()
            outputStream?.closeQuietly()
            buf?.fill(0x0)
        }

        logger.debug { "Secret key unwrapped from file $file." }
        emitter.onSuccess(secretKey)
    }

    /**
     * Returns the file containing a secret key.
     *
     * @param  alias Alias of the key.
     * @return       The file path.
     */
    private fun getKeystoreFile(alias: String): File {
        val dir: File

        if (alias.indexOfAny(charArrayOf('/', '.'), 0, true) >= 0 ||
                alias.endsWith(CryptoProviderJellyBeanMR2.EXT_KEYSTORE, true)) {
            throw applicationException {
                message(R.string.it_scoppelletti_security_err_aliasInvalid) {
                    arguments {
                        add(alias)
                    }
                }
            }
        }

        dir = File(ioProvider.noBackupFilesDir, CryptoProviderJellyBeanMR2.DIR)
        dir.mkdir()

        return File(dir, alias + CryptoProviderJellyBeanMR2.EXT_KEYSTORE)
    }

    private companion object {
        const val DIR: String = "it-scoppelletti-security"
        const val EXT_KEYSTORE: String = ".ks"
        val logger: KLogger = KotlinLogging.logger {}
    }
}
