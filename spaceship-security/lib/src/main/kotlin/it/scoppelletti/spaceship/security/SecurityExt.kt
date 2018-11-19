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

/**
 * Operations for security.
 */
internal object SecurityExt {
    const val KEYSTORE_TYPE: String = "AndroidKeyStore"
    const val PROVIDER_ANDROID: String = "AndroidKeyStore"
    const val PROVIDER_BC: String = "BC"
    const val PROVIDER_OPENSSL: String = "AndroidOpenSSL"
    const val TAG_CN: String = "CN="
    const val TRANSFORMATION_AES: String = "AES/CBC/PKCS7Padding"
    const val TRANSFORMATION_RSA: String = "RSA/ECB/PKCS1Padding"

    const val KEY_ALGORITHM_AES: String = "AES"
    // KeyProperties.KEY_ALGORITHM_AES

    const val KEY_ALGORITHM_RSA: String = "RSA"
    // KeyProperties.KEY_ALGORITHM_RSA

    const val IV_SIZE: Int = 12

    const val AES_KEYSIZE: Int = 128
    // - Genymotion 2.12.1, Samsung Galaxy S7, Android 6.0.0
    // Default: 128

    const val RSA_KEYSIZE: Int = 2048
    // - Genymotion 2.12.1, Samsung Galaxy S4, Android 4.3
    // Default: 2048
}
