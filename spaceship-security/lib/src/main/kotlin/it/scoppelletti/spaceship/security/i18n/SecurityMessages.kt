/*
 * Copyright (C) 2019 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

package it.scoppelletti.spaceship.security.i18n

import it.scoppelletti.spaceship.i18n.MessageSpec
import java.io.File

/**
 * String resources.
 *
 * @since 1.0.0
 */
public interface SecurityMessages {

    fun errorAliasInvalid(alias: String): MessageSpec

    fun errorAliasNotCertificate(alias: String): MessageSpec

    fun errorAliasNotFound(alias: String): MessageSpec

    fun errorAliasNotPrivateKey(alias: String): MessageSpec

    fun errorAliasNotSecretKey(alias: String): MessageSpec

    fun errorCertificateNotFound(alias: String): MessageSpec

    fun errorLoadSecretKey(file: File): MessageSpec

    fun errorProviderNotFound(name: String): MessageSpec

    fun errorSaveSecretKey(file: File): MessageSpec

    fun errorSecretKeyNotFound(file: File): MessageSpec
}
