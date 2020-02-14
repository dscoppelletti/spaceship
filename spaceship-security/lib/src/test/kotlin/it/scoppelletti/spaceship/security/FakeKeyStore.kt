@file:Suppress("JoinDeclarationAndAssignment")

package it.scoppelletti.spaceship.security

import it.scoppelletti.spaceship.types.joinLines
import mu.KotlinLogging
import java.io.InputStream
import java.io.OutputStream
import java.security.Key
import java.security.KeyStore
import java.security.KeyStoreSpi
import java.security.PrivateKey
import java.security.Security
import java.security.cert.Certificate
import java.util.Collections
import java.util.Date
import java.util.Enumeration
import javax.crypto.SecretKey

private val PROVIDER = Security.getProvider(SecurityExtTest.PROVIDER_SUN)
private val logger = KotlinLogging.logger {}

class FakeKeyStore(
        type: String,
        currentTime: () -> Date
) : KeyStore(FakeKeyStoreSpi(currentTime), PROVIDER, type)

private class FakeKeyStoreSpi(
        private val currentTime: () -> Date
) : KeyStoreSpi() {

    private val aliasMap: MutableMap<String, Date> = mutableMapOf()
    private val keyMap: MutableMap<String, Key> = mutableMapOf()
    private val certMap: MutableMap<String, Certificate> = mutableMapOf()

    override fun engineStore(stream: OutputStream?, password: CharArray?) {
        throw NotImplementedError()
    }

    override fun engineLoad(stream: InputStream?, password: CharArray?) {
        logger.debug("engineLoad")
    }

    override fun engineSize(): Int {
        return aliasMap.size
    }

    override fun engineGetEntry(
            alias: String?,
            protParam: KeyStore.ProtectionParameter?
    ): KeyStore.Entry? {
        val key: Key?
        val cert: Certificate?

        key = engineGetKey(alias, null)
        return when (key) {
            is PrivateKey -> {
                cert = engineGetCertificate(alias)
                KeyStore.PrivateKeyEntry(key, arrayOf(cert))
            }

            is SecretKeyDelegator -> {
                key.ckeckValidity(currentTime())
                KeyStore.SecretKeyEntry(key.delegate)
            }

            is SecretKey -> {
                KeyStore.SecretKeyEntry(key)
            }

            else -> null
        }
    }

    override fun engineSetKeyEntry(
            alias: String?,
            key: Key?,
            password: CharArray?,
            chain: Array<out Certificate>?
    ) {
        if (alias == null) {
            throw NullPointerException("Argument alias is null.")
        }
        if (key == null) {
            throw NullPointerException("Argument key is null.")
        }

        logger.debug { """engineSetKeyEntry(alias=$alias, key=$key,
            |password=$password,
            |chain=${chain?.contentToString()}""".trimMargin().joinLines() }

        aliasMap[alias] = currentTime()
        keyMap[alias] = key

        if (chain != null && chain.isNotEmpty()) {
            certMap[alias] = chain[0]
        } else {
            certMap.remove(alias)
        }
    }

    override fun engineSetKeyEntry(
            alias: String?,
            key: ByteArray?,
            chain: Array<out Certificate>?
    ) {
        throw NotImplementedError()
    }

    override fun engineSetCertificateEntry(alias: String?, cert: Certificate?) {
        throw NotImplementedError()
    }

    override fun engineAliases(): Enumeration<String> {
        return Collections.enumeration(aliasMap.keys)
    }

    override fun engineContainsAlias(alias: String?): Boolean {
        if (alias == null) {
            throw NullPointerException("Argument alias is null.")
        }

        return aliasMap.containsKey(alias)
    }

    override fun engineGetCreationDate(alias: String?): Date? {
        if (alias == null) {
            throw NullPointerException("Argument alias is null.")
        }

        return aliasMap[alias]
    }

    override fun engineIsKeyEntry(alias: String?): Boolean {
        if (alias == null) {
            throw NullPointerException("Argument alias is null.")
        }

        return keyMap.containsKey(alias)
    }

    override fun engineGetKey(alias: String?, password: CharArray?): Key? {
        if (alias == null) {
            throw NullPointerException("Argument alias is null.")
        }

        return keyMap[alias]
    }

    override fun engineIsCertificateEntry(alias: String?): Boolean {
        if (alias == null) {
            throw NullPointerException("Argument alias is null.")
        }

        return certMap.containsKey(alias)
    }

    override fun engineGetCertificate(alias: String?): Certificate? {
        if (alias == null) {
            throw NullPointerException("Argument alias is null.")
        }

        return certMap[alias]
    }

    override fun engineGetCertificateChain(alias: String?): Array<Certificate> {
        throw NotImplementedError()
    }

    override fun engineGetCertificateAlias(cert: Certificate?): String {
        throw NotImplementedError()
    }

    override fun engineDeleteEntry(alias: String?) {
        throw NotImplementedError()
    }
}
