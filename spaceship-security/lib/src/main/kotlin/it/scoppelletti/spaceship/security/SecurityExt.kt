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
    const val KEYSTORE_TYPE = "AndroidKeyStore"
    const val PROVIDER_ANDROID = "AndroidKeyStore"
    const val PROVIDER_BC = "BC"
    const val PROVIDER_OPENSSL = "AndroidOpenSSL"
    const val TAG_CN = "CN="
    const val TRANSFORMATION_AES = "AES/CBC/PKCS7Padding"
    const val TRANSFORMATION_RSA = "RSA/ECB/PKCS1Padding"

    const val KEY_ALGORITHM_AES = "AES"
    // KeyProperties.KEY_ALGORITHM_AES

    const val KEY_ALGORITHM_RSA = "RSA"
    // KeyProperties.KEY_ALGORITHM_RSA

    const val IV_SIZE = 12

    const val AES_KEYSIZE = 128
    // - Genymotion 2.12.1, Samsung Galaxy S7, Android 6.0.0
    // Default: 128

    const val RSA_KEYSIZE = 2048
    // - Genymotion 2.12.1, Samsung Galaxy S4, Android 4.3
    // Default: 2048
}
