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

import android.os.Build
import androidx.annotation.RequiresApi
import java.io.InputStream
import java.io.OutputStream
import javax.crypto.Cipher
import javax.crypto.CipherOutputStream

/**
 * Implementation of the `CipherIOProvider` interface for Nougat.
 */
@RequiresApi(Build.VERSION_CODES.N)
internal class CipherIOProviderNougat : CipherIOProvider {

    override fun cipherInputStream(
            inputStream: InputStream,
            cipher: Cipher
    ): InputStream = CipherInputStreamAndroid(inputStream, cipher)
        // - API 24, 25
        // If encrypted data are corrupted, the AEADBadTagException exception
        // is swallowed by the decrypting OpenJDK7 CipherInputStream and the
        // result is an empty input stream:
        // I need use a newer version of the CipherInputStream class.

    override fun cipherOutputStream(
            outputStream: OutputStream,
            cipher: Cipher
    ): OutputStream = CipherOutputStream(outputStream, cipher)
}