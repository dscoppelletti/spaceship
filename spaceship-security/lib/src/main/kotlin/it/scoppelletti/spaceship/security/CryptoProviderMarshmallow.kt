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
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import it.scoppelletti.spaceship.ApplicationException
import it.scoppelletti.spaceship.security.i18n.SecurityMessages
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import java.security.GeneralSecurityException
import java.security.KeyStore
import java.security.SecureRandom
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

/**
 * Implementation of the `CryptoProvider` interface for Marshmallow.
 */
@RequiresApi(Build.VERSION_CODES.M)
internal class CryptoProviderMarshmallow(
        private val random: SecureRandom,
        private val securityBridge: SecurityBridge,
        private val securityMessages: SecurityMessages
) : CryptoProvider by DefaultCryptoProvider(random, securityBridge,
        securityMessages) {

    @Throws(GeneralSecurityException::class)
    override suspend fun newSecretKey(
            alias: String,
            expire: Int
    ): SecretKey = withContext(Dispatchers.Default) {
        val secretKey: SecretKey
        val keyGen: KeyGenerator
        val params: AlgorithmParameterSpec

        params = securityBridge.createKeyGenParameterSpec(alias, expire)

        if (!isActive) {
            throw CancellationException()
        }

        keyGen = securityBridge.createKeyGenerator(
                KeyProperties.KEY_ALGORITHM_AES,
                SecurityExt.PROVIDER_ANDROID)
                .apply {
                    init(params, random)
                }

        if (!isActive) {
            throw CancellationException()
        }

        secretKey = keyGen.generateKey()
        logger.debug { "New secret key $alias generated." }
        secretKey
    }

    @Throws(GeneralSecurityException::class)
    override suspend fun loadSecretKey(alias: String): SecretKey =
            withContext(Dispatchers.Default) {
                val keyStore: KeyStore
                val entry: KeyStore.Entry?

                keyStore = securityBridge.createKeyStore(
                        SecurityExt.KEYSTORE_TYPE)

                if (!isActive) {
                    throw CancellationException()
                }

                // - Android Emulator 28.0.16, Nexus 5X, Android 8.1
                // If the key is expired, the method getEntry issues an
                // exception UnrecoverableKeyException (Failed to obtain
                // information about key) with an inner exception
                // KeyStoreException (Invalid key blob).
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
                    throw ApplicationException(
                            securityMessages.errorAliasNotFound(alias))
                }

                if (entry !is KeyStore.SecretKeyEntry) {
                    throw ApplicationException(
                            securityMessages.errorAliasNotSecretKey(alias))
                }

                logger.debug { "Secret key $alias loaded." }
                entry.secretKey
            }

    private companion object {
        val logger = KotlinLogging.logger {}
    }
}
