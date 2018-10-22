/*
 * Copyright (C) 2018 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import it.scoppelletti.spaceship.types.TimeProvider
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.spec.AlgorithmParameterSpec
import java.util.Calendar
import java.util.Date
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.security.auth.x500.X500Principal

/**
 * Default implementation of the `CipherProvider` interface.
 *
 * @constructor              Constructor.
 * @param       context      Context.
 * @param       timeProvider Provides components for operations on dates and
 *                           times.
 */
internal class DefaultCipherProvider(
        private val context: Context,
        private val timeProvider: TimeProvider
) : CipherProvider {

    override fun createKeyGenerator(
            algorithm: String,
            provider: String
    ): KeyGenerator = KeyGenerator.getInstance(algorithm, provider)

    @RequiresApi(Build.VERSION_CODES.M)
    override fun createKeyGenParameterSpec(
            keystoreAlias: String,
            expire: Int
    ): AlgorithmParameterSpec {
        val endDate: Date

        endDate = timeProvider.currentTime()
                .apply {
                    add(Calendar.DATE, expire)
                }
                .time

        // - OnePlus A3003, OxygenOS 4.1.6, Android 7.1.1
        // If I set the keyValidityStart, the init method of the Cipher
        // object often throws a KeyNotYetValidException exception: I should
        // wait in debug mode for few seconds to avoid that.
        return KeyGenParameterSpec.Builder(keystoreAlias,
                KeyProperties.PURPOSE_ENCRYPT or
                        KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(
                        KeyProperties.ENCRYPTION_PADDING_NONE)
                .setRandomizedEncryptionRequired(false)
                .setUserAuthenticationRequired(false)
                .setKeyValidityEnd(endDate)
                .build()
    }

    override fun createKeyPairGenerator(
            algorithm: String,
            provider: String
    ): KeyPairGenerator = KeyPairGenerator.getInstance(algorithm, provider)

    @Suppress("deprecation")
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    override fun createKeyPairGenParameterSpec(
            alias: String,
            expire: Int
    ): AlgorithmParameterSpec {
        val serialNumber: Long
        val startDate: Calendar
        val endDate: Calendar
        val subject: X500Principal

        subject = X500Principal(SecurityExt.TAG_CN + alias)
        serialNumber = System.currentTimeMillis()

        startDate = timeProvider.currentTime()
        endDate = startDate.clone() as Calendar
        endDate.add(Calendar.DATE, expire)

        return android.security.KeyPairGeneratorSpec.Builder(context)
                .setAlias(alias)
                .setSubject(subject)
                .setSerialNumber(serialNumber.toBigInteger())
                .setStartDate(startDate.time)
                .setEndDate(endDate.time)
                .build()
    }

    override fun createCipher(transformation: String): Cipher =
            Cipher.getInstance(transformation)

    override fun createCipher(
            transformation: String,
            provider: String
    ): Cipher = Cipher.getInstance(transformation, provider)

    override fun createKeyStore(type: String): KeyStore =
            KeyStore.getInstance(type).apply {
                load(null)
            }
}

