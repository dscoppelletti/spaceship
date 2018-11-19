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

import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException

/**
 * Provides components for encryption/decryption.
 */
internal interface SecurityBridge {

    /**
     * Creates a new `Cipher` instance.
     *
     * @param  transformation Name of the transformation.
     * @return                The new object.
     */
    @Throws(NoSuchAlgorithmException::class, NoSuchPaddingException::class)
    fun createCipher(transformation: String): Cipher

    /**
     * Creates a new `Cipher` instance.
     *
     * @param  transformation Name of the transformation.
     * @param  provider       Name of the provider.
     * @return                The new object.
     */
    @Throws(NoSuchAlgorithmException::class, NoSuchProviderException::class,
            NoSuchPaddingException::class)
    fun createCipher(transformation: String, provider: String): Cipher

    /**
     * Creates the parametes object for a cipher operation.
     *
     * @param  iv `IV`.
     * @return    The new object.
     */
    fun createCipherParameterSpec(iv: ByteArray): AlgorithmParameterSpec

    /**
     * Creates a new `KeyGenerator` instance.
     *
     * @param  algorithm Name of the algorithm.
     * @param  provider  Name of the provider.
     * @return           The new object.
     */
    @Throws(NoSuchAlgorithmException::class, NoSuchProviderException::class)
    fun createKeyGenerator(
            algorithm: String,
            provider: String
    ): KeyGenerator

    /**
     * Creates the parameters object for the generation of a key.
     *
     * @param  keystoreAlias Alias of the entry in which the generated key will
     *                       appear in Android KeyStore.
     * @param  expire        Expiration of the key (days).
     * @return               The new object.
     */
    fun createKeyGenParameterSpec(
            keystoreAlias: String,
            expire: Int
    ): AlgorithmParameterSpec

    /**
     * Creates a new `KeyPairGenerator` instance.
     *
     * @param  algorithm Name of the algorithm.
     * @param  provider  Name of the provider.
     * @return           The new object.
     */
    @Throws(NoSuchAlgorithmException::class, NoSuchProviderException::class)
    fun createKeyPairGenerator(
            algorithm: String,
            provider: String
    ): KeyPairGenerator

    /**
     * Creates the parameters object for the generation of a key pair.
     *
     * @param  alias  Alias to be used to retrieve the key later from a
     *                `KeyStore` instance using the `AndroidKeyStore` provider.
     * @param  expire Expiration of the key the self-signed certificate of the
     *                generated key pair (days).
     * @return        The new object.
     */
    fun createKeyPairGenParameterSpec(
            alias: String,
            expire: Int
    ): AlgorithmParameterSpec

    /**
     * Creates a new `KeyStore` instance (already initialized).
     *
     * @param  type Type of the `KeyStore`.
     * @return      The new object.
     */
    @Throws(KeyStoreException::class)
    fun createKeyStore(type: String): KeyStore
}