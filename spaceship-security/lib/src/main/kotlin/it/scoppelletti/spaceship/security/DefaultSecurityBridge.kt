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

@file:Suppress("JoinDeclarationAndAssignment")

package it.scoppelletti.spaceship.security

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import org.threeten.bp.Clock
import org.threeten.bp.ZonedDateTime
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.spec.AlgorithmParameterSpec
import java.util.Date
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.IvParameterSpec
import javax.security.auth.x500.X500Principal

private const val YEAR_MAX = 9999

/**
 * Default implementation of the `CipherProvider` interface.
 */
internal class DefaultSecurityBridge(
        private val context: Context,
        private val clock: Clock
) : SecurityBridge {

    override fun createCipher(transformation: String): Cipher =
            Cipher.getInstance(transformation)

    override fun createCipher(
            transformation: String,
            provider: String
    ): Cipher = Cipher.getInstance(transformation, provider)

    override fun createCipherParameterSpec(
            iv: ByteArray
    ): AlgorithmParameterSpec = IvParameterSpec(iv)

    override fun createKeyGenerator(
            algorithm: String,
            provider: String
    ): KeyGenerator = KeyGenerator.getInstance(algorithm, provider)

    @RequiresApi(Build.VERSION_CODES.M)
    override fun createKeyGenParameterSpec(
            keystoreAlias: String,
            expire: Int
    ): AlgorithmParameterSpec {
        val endDate: ZonedDateTime
        val builder: KeyGenParameterSpec.Builder

        builder = KeyGenParameterSpec.Builder(keystoreAlias,
                KeyProperties.PURPOSE_ENCRYPT or
                        KeyProperties.PURPOSE_DECRYPT)
                .setKeySize(SecurityExt.AES_KEYSIZE)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(
                        KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setRandomizedEncryptionRequired(true)
                .setUserAuthenticationRequired(false)

        if (expire > 0) {
            // - OnePlus A3003, OxygenOS 4.1.6, Android 7.1.1
            // If I set the keyValidityStart, the init method of the Cipher
            // object often throws a KeyNotYetValidException exception: I should
            // wait in debug mode for few seconds to avoid that.
            endDate = ZonedDateTime.now(clock).plusDays(expire.toLong())
            builder.setKeyValidityEnd(Date(endDate.toEpochSecond() * 1000L))
        }

        return builder.build()
    }

    override fun createKeyPairGenerator(
            algorithm: String,
            provider: String
    ): KeyPairGenerator = KeyPairGenerator.getInstance(algorithm, provider)

    @Suppress("deprecation")
    @SuppressLint("WrongConstant")
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    override fun createKeyPairGenParameterSpec(
            alias: String,
            expire: Int
    ): AlgorithmParameterSpec {
        val serialNumber: Long
        val startDate: ZonedDateTime
        val endDate: ZonedDateTime
        val subject: X500Principal
        val builder: android.security.KeyPairGeneratorSpec.Builder

        subject = X500Principal(SecurityExt.TAG_CN + alias)
        serialNumber = System.currentTimeMillis()

        startDate = ZonedDateTime.now(clock)
        endDate = if (expire > 0) startDate.plusDays(expire.toLong()) else
            // Simulate no expiration
            startDate.withYear(YEAR_MAX)

        builder = android.security.KeyPairGeneratorSpec.Builder(context)
                .setAlias(alias)
                .setSubject(subject)
                .setSerialNumber(serialNumber.toBigInteger())
                .setStartDate(Date(startDate.toEpochSecond() * 1000L))
                .setEndDate(Date(endDate.toEpochSecond() * 1000L))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            builder.setKeyType(SecurityExt.KEY_ALGORITHM_RSA)
                    .setKeySize(SecurityExt.RSA_KEYSIZE)
        }

        return builder.build()
    }

    override fun createKeyStore(type: String): KeyStore =
            KeyStore.getInstance(type).apply {
                load(null)
            }
}
