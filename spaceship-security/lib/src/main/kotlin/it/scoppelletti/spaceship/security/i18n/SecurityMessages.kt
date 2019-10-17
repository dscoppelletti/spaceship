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

import it.scoppelletti.spaceship.i18n.AndroidResourceMessageSpec
import it.scoppelletti.spaceship.i18n.MessageSpec
import it.scoppelletti.spaceship.security.R
import java.io.File

/**
 * String resources.
 *
 * @since 1.0.0
 */
public object SecurityMessages {

    public fun errorAliasInvalid(alias: String): MessageSpec =
            AndroidResourceMessageSpec(
                    R.string.it_scoppelletti_security_err_aliasInvalid,
                    arrayOf(alias))

    public fun errorAliasNotCertificate(alias: String): MessageSpec =
            AndroidResourceMessageSpec(
                    R.string.it_scoppelletti_security_err_aliasNotCertificate,
                    arrayOf(alias))

    public fun errorAliasNotFound(alias: String): MessageSpec =
            AndroidResourceMessageSpec(
                    R.string.it_scoppelletti_security_err_aliasNotFound,
                    arrayOf(alias))

    public fun errorAliasNotPrivateKey(alias: String): MessageSpec =
            AndroidResourceMessageSpec(
                    R.string.it_scoppelletti_security_err_aliasNotPrivateKey,
                    arrayOf(alias))

    public fun errorAliasNotSecretKey(alias: String): MessageSpec =
            AndroidResourceMessageSpec(
                    R.string.it_scoppelletti_security_err_aliasNotSecretKey,
                    arrayOf(alias))

    public fun errorCertificateNotFound(alias: String): MessageSpec =
            AndroidResourceMessageSpec(
                    R.string.it_scoppelletti_security_err_certificateNotFound,
                    arrayOf(alias))

    public fun errorLoadSecretKey(file: File): MessageSpec =
            AndroidResourceMessageSpec(
                    R.string.it_scoppelletti_security_err_loadSecretKey,
                    arrayOf(file))

    public fun errorProviderNotFound(name: String): MessageSpec =
            AndroidResourceMessageSpec(
                    R.string.it_scoppelletti_security_err_providerNotFound,
                    arrayOf(name))

    public fun errorSaveSecretKey(file: File): MessageSpec =
            AndroidResourceMessageSpec(
                    R.string.it_scoppelletti_security_err_saveSecretKey,
                    arrayOf(file))

    public fun errorSecretKeyNotFound(file: File): MessageSpec =
            AndroidResourceMessageSpec(
                    R.string.it_scoppelletti_security_err_secretKeyNotFound,
                    arrayOf(file))

}
