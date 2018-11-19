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
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import io.reactivex.Single
import io.reactivex.SingleEmitter
import it.scoppelletti.spaceship.applicationException
import mu.KLogger
import mu.KotlinLogging
import java.security.GeneralSecurityException
import java.security.KeyStore
import java.security.SecureRandom
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

/**
 * Implementation of the `CryptoProvider` interface for Marshmallow.
 *
 * @constructor                Constructor.
 * @param       random         CSRNG.
 * @param       securityBridge Bridge to security API.
 */
@RequiresApi(Build.VERSION_CODES.M)
internal class CryptoProviderMarshmallow(
        private val random: SecureRandom,
        private val securityBridge: SecurityBridge
) : CryptoProvider by DefaultCryptoProvider(random, securityBridge) {

    override fun newSecretKey(
            alias: String,
            expire: Int
    ): Single<SecretKey> =
            Single.create<SecretKey> { emitter ->
                onNewSecretKeySubscribe(emitter, alias, expire)
            }

    override fun loadSecretKey(alias: String): Single<SecretKey> =
            Single.create<SecretKey> { emitter ->
                onLoadSecretKeySubscribe(emitter, alias)
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

        params = securityBridge.createKeyGenParameterSpec(alias, expire)

        if (emitter.isDisposed) {
            return
        }

        try {
            keyGen = securityBridge.createKeyGenerator(
                    KeyProperties.KEY_ALGORITHM_AES,
                    SecurityExt.PROVIDER_ANDROID)
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
            emitter: SingleEmitter<SecretKey>,
            alias: String
    ) {
        val keyStore: KeyStore
        val entry: KeyStore.Entry?

        try {
            keyStore = securityBridge.createKeyStore(SecurityExt.KEYSTORE_TYPE)

            if (emitter.isDisposed) {
                return
            }

            // - Android Emulator 28.0.16, Nexus 5X, Android 8.1
            // If the key is expired, the method getEntry issues an exception
            // UnrecoverableKeyException (Failed to obtain information about
            // key) with an inner exception KeyStoreException (Invalid key
            // blob).
            //
            // - Genymotion 2.12.1, Samsung Galaxy S7, Android 6.0.0
            // - Genymotion 2.12.1, Samsung Galaxy S8, Android 7.0.0
            // - Genymotion 2.12.1, Samsung Galaxy S7, Android 7.1.0
            // - Genymotion 2.12.1, Samsung Galaxy S8, Android 8.0
            // - Android Emulator 28.0.16, Pixel, Android 9.0
            // If the key is expired, the exception KeyExpiredException (Key
            // expired) will be issued by method init of object Cipher.
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

            if (entry !is KeyStore.SecretKeyEntry) {
                throw applicationException {
                    message(R.string.it_scoppelletti_security_err_aliasNotSecretKey) {
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

        logger.debug { "Secret key $alias loaded." }
        emitter.onSuccess(entry.secretKey)
    }

    private companion object {
        val logger: KLogger = KotlinLogging.logger {}
    }
}