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

@file:Suppress("RedundantVisibilityModifier")

package it.scoppelletti.spaceship.io

import android.content.Context
import android.util.Base64
import android.util.Base64InputStream
import android.util.Base64OutputStream
import androidx.core.content.ContextCompat
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

/**
 * Default implementation of the `IOProvider` interface.
 *
 * @since 1.0.0
 */
public class DefaultIOProvider @Inject constructor(
        private val context: Context
) : IOProvider {

    override val noBackupFilesDir: File
        get() = ContextCompat.getNoBackupFilesDir(context)!!

    override fun base64InputStream(inputStream: InputStream): InputStream =
            Base64InputStream(inputStream, Base64.DEFAULT)

    override fun base64OutputStream(outputStream: OutputStream): OutputStream =
            Base64OutputStream(outputStream, Base64.DEFAULT)
}