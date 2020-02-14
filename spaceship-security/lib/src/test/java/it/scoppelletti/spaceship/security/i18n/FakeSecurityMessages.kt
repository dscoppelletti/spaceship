package it.scoppelletti.spaceship.security.i18n

import it.scoppelletti.spaceship.i18n.I18NProvider
import it.scoppelletti.spaceship.i18n.MessageSpec
import java.io.File

class FakeSecurityMessages : SecurityMessages {

    override fun errorAliasInvalid(alias: String): MessageSpec =
            FakeMessageSpec("Invalid alias $alias.")

    override fun errorAliasNotCertificate(alias: String): MessageSpec =
            FakeMessageSpec("Entry $alias is not a X509 certificate.")

    override fun errorAliasNotFound(alias: String): MessageSpec =
            FakeMessageSpec("Entry $alias not found.")

    override fun errorAliasNotPrivateKey(alias: String): MessageSpec =
            FakeMessageSpec("Entry $alias is not a private key.")

    override fun errorAliasNotSecretKey(alias: String): MessageSpec =
            FakeMessageSpec("Entry $alias is not a secret key")

    override fun errorCertificateNotFound(alias: String): MessageSpec =
            FakeMessageSpec("Certificate $alias not found.")

    override fun errorLoadSecretKey(file: File): MessageSpec =
            FakeMessageSpec("Failed to load secret key from file $file.")

    override fun errorProviderNotFound(name: String): MessageSpec =
            FakeMessageSpec("Provider $name not found.")

    override fun errorSaveSecretKey(file: File): MessageSpec =
            FakeMessageSpec("Failed to save secret key in file $file.")

    override fun errorSecretKeyNotFound(file: File): MessageSpec =
            FakeMessageSpec("Secret key not found (file $file).")
}

private data class FakeMessageSpec(
        val message: String
) : MessageSpec {

    override fun buildMessage(i18nProvider: I18NProvider): String = message
}
