package it.scoppelletti.spaceship.security

import mu.KotlinLogging
import java.security.InvalidKeyException
import java.security.KeyStore
import java.security.SecureRandom
import java.security.Security
import java.security.spec.AlgorithmParameterSpec
import java.util.Date
import javax.crypto.KeyGenerator
import javax.crypto.KeyGeneratorSpi
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.SecretKeySpec

private val PROVIDER = Security.getProvider(SecurityExtTest.PROVIDER_SUN)
private val logger = KotlinLogging.logger {}

class StubKeyGenerator(
        algorithm: String,
        keyStore: KeyStore
) : KeyGenerator(StubKeyGeneratorSpi(algorithm, keyStore), PROVIDER,
        algorithm) {

    companion object {

        // The encoded form is not always identical to the raw data!
        val RAW: ByteArray = ByteArray(8) { 11 }
        val ENCODED: ByteArray = ByteArray(8) { 11 }
    }
}

private class StubKeyGeneratorSpi(
        private val algorithm: String,
        private val keyStore: KeyStore
) : KeyGeneratorSpi() {

    private var spec: FakeKeyGenParameterSpec? = null

    override fun engineInit(keysize: Int, random: SecureRandom?) {
        logger.debug { "engineInit(keysize=$keysize)" }
    }

    override fun engineInit(random: SecureRandom?) {
        throw NotImplementedError()
    }

    override fun engineInit(
            params: AlgorithmParameterSpec?,
            random: SecureRandom?
    ) {
        spec = params as? FakeKeyGenParameterSpec
        logger.debug { "engineInit(alias=${spec?.keystoreAlias})" }
    }

    override fun engineGenerateKey(): SecretKey {
        val key: SecretKey
        val keyFactory: SecretKeyFactory
        val keySpec: SecretKeySpec

        try {
            keyFactory = SecretKeyFactory.getInstance(algorithm, PROVIDER)
            keySpec = SecretKeySpec(StubKeyGenerator.RAW, algorithm)
            key = keyFactory.generateSecret(keySpec)

            if (spec != null) {
                keyStore.setKeyEntry(spec?.keystoreAlias,
                        SecretKeyDelegator(key, spec?.keyValidityStart,
                                spec?.keyValidityEnd), null, null)
            }
        } finally {
            spec = null
        }

        return key
    }
}

class SecretKeyDelegator(
        val delegate: SecretKey,
        private val keyValidityStart: Date?,
        private val keyValidityEnd: Date?
) : SecretKey by delegate {

    fun ckeckValidity(date: Date) {
        if (keyValidityStart != null && date.before(keyValidityStart)) {
            throw InvalidKeyException("Key not yet valid.")
        }
        if (keyValidityEnd != null && date.after(keyValidityEnd)) {
            throw InvalidKeyException("Key expired.")
        }
    }
}

class FakeKeyGenParameterSpec(
        val keystoreAlias: String,
        val keyValidityStart: Date,
        val keyValidityEnd: Date?
) : AlgorithmParameterSpec
