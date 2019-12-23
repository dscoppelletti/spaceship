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
import androidx.annotation.RequiresApi
import it.scoppelletti.spaceship.ApplicationException
import it.scoppelletti.spaceship.io.IOProvider
import it.scoppelletti.spaceship.io.closeQuietly
import it.scoppelletti.spaceship.security.i18n.SecurityMessages
import it.scoppelletti.spaceship.types.joinLines
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import okio.BufferedSink
import okio.BufferedSource
import okio.ByteString
import okio.Okio
import okio.Sink
import okio.Source
import org.threeten.bp.Clock
import org.threeten.bp.ZonedDateTime
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.security.GeneralSecurityException
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.Certificate
import java.security.cert.X509Certificate
import java.security.spec.AlgorithmParameterSpec
import java.util.Date
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

/**
 * Implementation of the `CryptoProvider` interface for JellyBean (MR2).
 */
@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
internal class CryptoProviderJellyBeanMR2(
        private val ioProvider: IOProvider,
        private val clock: Clock,
        private val random: SecureRandom,
        private val securityBridge: SecurityBridge,
        private val securityMessages: SecurityMessages
) : CryptoProvider by DefaultCryptoProvider(random, securityBridge,
        securityMessages) {

    @Throws(GeneralSecurityException::class)
    override suspend fun newSecretKey(
            alias: String,
            expire: Int
    ): SecretKey = withContext(Dispatchers.IO) {
        val keyPair: KeyPair

        keyPair = newKeyPair(alias, expire)

        if (!isActive) {
            throw CancellationException()
        }

        newSecretKey(alias, keyPair)
    }

    @Throws(GeneralSecurityException::class)
    override suspend fun loadSecretKey(alias: String): SecretKey =
            withContext(Dispatchers.IO) {
                val keyPair: KeyPair

                keyPair = loadKeyPair(alias)

                if (!isActive) {
                    throw CancellationException()
                }

                loadSecretKey(alias, keyPair)
            }

    /**
     * Generates a new key pair.
     *
     * @param  alias  Alias of the key.
     * @param  expire Expiration of the key (days).
     * @return        The new key pair.
     */
    @Throws(GeneralSecurityException::class)
    private fun newKeyPair(alias: String, expire: Int): KeyPair {
        val keyPair: KeyPair
        val keyGen: KeyPairGenerator
        val params: AlgorithmParameterSpec

        params = securityBridge.createKeyPairGenParameterSpec(alias, expire)
        keyGen = securityBridge.createKeyPairGenerator(
                SecurityExt.KEY_ALGORITHM_RSA, SecurityExt.PROVIDER_ANDROID)
                .apply {
                    initialize(params, random)
                }

        keyPair = keyGen.generateKeyPair()
        logger.debug { "New key pair $alias generated." }

        return keyPair
    }

    /**
     * Loads a key pair.
     *
     * @param  alias Alias of the key.
     * @return       The key pair.
     */
    @Throws(GeneralSecurityException::class)
    private fun loadKeyPair(alias: String): KeyPair {
        val keyStore: KeyStore
        val cert: Certificate?
        val entry: KeyStore.Entry?

        keyStore = securityBridge.createKeyStore(SecurityExt.KEYSTORE_TYPE)

        cert = keyStore.getCertificate(alias)
        if (cert == null) {
            throw ApplicationException(
                    securityMessages.errorCertificateNotFound(alias))
        }

        if (cert !is X509Certificate) {
            throw ApplicationException(
                    securityMessages.errorAliasNotCertificate(alias))
        }

        logger.debug { """Found certificate $alias valid from ${cert.notBefore}
            |to ${cert.notAfter}.""".trimMargin().joinLines() }

        // If the certificate is expired, the method checkValidity issues
        // a CertificateExpiredException.
        // It seems that the expiration of the certificate is ignored by
        // any further use of the public or private key.
        cert.checkValidity(
                Date(ZonedDateTime.now(clock).toEpochSecond() * 1000L))

        entry = keyStore.getEntry(alias, null)
        if (entry == null) {
            throw ApplicationException(
                    securityMessages.errorAliasNotFound(alias))
        }

        if (entry !is KeyStore.PrivateKeyEntry) {
            throw ApplicationException(
                    securityMessages.errorAliasNotPrivateKey(alias))
        }

        logger.debug { "Key pair $alias loaded." }
        return KeyPair(cert.publicKey, entry.privateKey)
    }

    /**
     * Generates a new secret key.
     *
     * @param  alias   Alias of the key.
     * @param  keyPair Key pair.
     * @return         The new key.
     */
    private fun newSecretKey(alias: String, keyPair: KeyPair): SecretKey {
        val file: File
        val secretKey: SecretKey
        val keyGen: KeyGenerator
        val cipher: Cipher
        val source: ByteString
        var key: ByteArray? = null
        var sink: Sink? = null
        var bufSink: BufferedSink? = null

        file = getKeystoreFile(alias)

        try {
            keyGen = securityBridge.createKeyGenerator(
                    SecurityExt.KEY_ALGORITHM_AES, SecurityExt.PROVIDER_BC)
                    .apply {
                        init(SecurityExt.AES_KEYSIZE, random)
                    }

            secretKey = keyGen.generateKey()
            logger.debug("New secret key generated.")

            cipher = securityBridge.createCipher(SecurityExt.TRANSFORMATION_RSA,
                    SecurityExt.PROVIDER_OPENSSL)
                    .apply {
                        init(Cipher.WRAP_MODE, keyPair.public)
                    }

            key = cipher.wrap(secretKey)
            source = ByteString.of(key!!, 0, key.size)

            sink = Okio.sink(file)
            bufSink = Okio.buffer(sink!!)
            sink = null

            bufSink.writeUtf8(source.base64())
            bufSink.flush()
        } catch (ex: Exception) {
            // GeneralSecurityException|IllegalStateException|IOException
            throw ApplicationException(
                    securityMessages.errorSaveSecretKey(file), ex)
        } finally {
            sink?.closeQuietly()
            bufSink?.closeQuietly()
            key?.fill(0x0)
        }

        logger.debug { "Secret key wrapped in file $file." }
        return secretKey
    }

    /**
     * Loads a secret key.
     *
     * @param  alias   Alias of the key.
     * @param  keyPair Key pair.
     * @return         The key.
     */
    private fun loadSecretKey(alias: String, keyPair: KeyPair): SecretKey {
        val file: File
        val secretKey: SecretKey
        val cipher: Cipher
        val sink: ByteString?
        var buf: ByteArray? = null
        var source: Source? = null
        var bufSource: BufferedSource? = null

        file = getKeystoreFile(alias)

        try {
            cipher = securityBridge.createCipher(SecurityExt.TRANSFORMATION_RSA,
                    SecurityExt.PROVIDER_OPENSSL)
                    .apply {
                        init(Cipher.UNWRAP_MODE, keyPair.private)
                    }

            source = Okio.source(file)
            bufSource = Okio.buffer(source!!)
            source = null

            sink = ByteString.decodeBase64(bufSource.readUtf8())
            if (sink == null) {
                throw IOException("No Base64 data.")
            }

            buf = sink.toByteArray()
            secretKey = cipher.unwrap(buf, SecurityExt.KEY_ALGORITHM_AES,
                    Cipher.SECRET_KEY) as SecretKey
        } catch (ex: FileNotFoundException) {
            throw ApplicationException(
                    securityMessages.errorSecretKeyNotFound(file), ex)
        } catch (ex: Exception) {
            // GeneralSecurityException|IllegalStateException|IOException
            throw ApplicationException(
                    securityMessages.errorLoadSecretKey(file), ex)
        } finally {
            source?.closeQuietly()
            bufSource?.closeQuietly()
            buf?.fill(0x0)
        }

        logger.debug { "Secret key unwrapped from file $file." }
        return secretKey
    }

    /**
     * Returns the file containing a secret key.
     *
     * @param  alias Alias of the key.
     * @return       The file path.
     */
    @Suppress("RemoveRedundantQualifierName")
    private fun getKeystoreFile(alias: String): File {
        val dir: File

        if (alias.indexOfAny(charArrayOf('/', '.'), 0, true) >= 0 ||
                alias.endsWith(CryptoProviderJellyBeanMR2.EXT_KEYSTORE, true)) {
            throw ApplicationException(
                    securityMessages.errorAliasInvalid(alias))
        }

        dir = File(ioProvider.noBackupFilesDir, CryptoProviderJellyBeanMR2.DIR)
        dir.mkdir()

        return File(dir, alias + CryptoProviderJellyBeanMR2.EXT_KEYSTORE)
    }

    private companion object {
        const val DIR = "it-scoppelletti-security"
        const val EXT_KEYSTORE = ".ks"
        val logger = KotlinLogging.logger {}
    }
}
