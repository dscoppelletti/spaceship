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

import android.content.res.Resources
import it.scoppelletti.spaceship.i18n.AndroidResourceMessageSpec
import it.scoppelletti.spaceship.i18n.MessageSpec
import it.scoppelletti.spaceship.security.R
import java.io.File
import javax.inject.Inject

/**
 * Default implementation of the `SecurityMessages` interface.
 *
 * @since 1.0.0
 */
public class DefaultSecurityMessages @Inject constructor(
        private val resources: Resources
) : SecurityMessages {

    override fun errorAliasInvalid(alias: String): MessageSpec =
            AndroidResourceMessageSpec.of(resources,
                    R.string.it_scoppelletti_security_err_aliasInvalid, alias)

    override fun errorAliasNotCertificate(alias: String): MessageSpec =
            AndroidResourceMessageSpec.of(resources,
                    R.string.it_scoppelletti_security_err_aliasNotCertificate,
                    alias)

    override fun errorAliasNotFound(alias: String): MessageSpec =
            AndroidResourceMessageSpec.of(resources,
                    R.string.it_scoppelletti_security_err_aliasNotFound, alias)

    override fun errorAliasNotPrivateKey(alias: String): MessageSpec =
            AndroidResourceMessageSpec.of(resources,
                    R.string.it_scoppelletti_security_err_aliasNotPrivateKey,
                    alias)

    override fun errorAliasNotSecretKey(alias: String): MessageSpec =
            AndroidResourceMessageSpec.of(resources,
                    R.string.it_scoppelletti_security_err_aliasNotSecretKey,
                    alias)

    override fun errorCertificateNotFound(alias: String): MessageSpec =
            AndroidResourceMessageSpec.of(resources,
                    R.string.it_scoppelletti_security_err_certificateNotFound,
                    alias)

    override fun errorLoadSecretKey(file: File): MessageSpec =
            AndroidResourceMessageSpec.of(resources,
                    R.string.it_scoppelletti_security_err_loadSecretKey, file)

    override fun errorProviderNotFound(name: String): MessageSpec =
            AndroidResourceMessageSpec.of(resources,
                    R.string.it_scoppelletti_security_err_providerNotFound,
                    name)

    override fun errorSaveSecretKey(file: File): MessageSpec =
            AndroidResourceMessageSpec.of(resources,
                    R.string.it_scoppelletti_security_err_saveSecretKey, file)

    override fun errorSecretKeyNotFound(file: File): MessageSpec =
            AndroidResourceMessageSpec.of(resources,
                    R.string.it_scoppelletti_security_err_secretKeyNotFound,
                    file)
}
