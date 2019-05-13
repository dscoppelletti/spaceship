package it.scoppelletti.spaceship.security

import mu.KotlinLogging
import java.math.BigInteger
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
import java.security.cert.Certificate
import java.security.cert.X509Certificate
import java.security.spec.AlgorithmParameterSpec
import java.security.spec.KeySpec
import java.security.spec.RSAPrivateKeySpec
import java.security.spec.RSAPublicKeySpec
import java.util.Date
import javax.security.auth.x500.X500Principal

private val MODULUS = BigInteger(ByteArray(64) { 3 })
private val PUBLIC_EXP = BigInteger(ByteArray(4) { 5 })
private val PRIVATE_EXP = BigInteger(ByteArray(64) { 7 })
private val logger = KotlinLogging.logger {}

class StubKeyPairGenerator(
        algorithm: String,
        private val keyStore: KeyStore
) : KeyPairGenerator(algorithm) {

    private var spec: FakeKeyPairGeneratorSpec? = null

    override fun initialize(
            params: AlgorithmParameterSpec?,
            random: SecureRandom?
    ) {
        spec = params as? FakeKeyPairGeneratorSpec
        logger.debug { "engineInit(alias=${spec?.alias})" }
    }

    override fun generateKeyPair(): KeyPair {
        val keyPair: KeyPair
        val publicKey: PublicKey
        val privateKey: PrivateKey
        val keyFactory: KeyFactory
        val cert: Certificate
        var keySpec: KeySpec
        val x509Cert: X509Certificate

        try {
            keyFactory = KeyFactory.getInstance(
                    SecurityExt.KEY_ALGORITHM_RSA)
            keySpec = RSAPublicKeySpec(MODULUS, PUBLIC_EXP)

            publicKey = keyFactory.generatePublic(keySpec)

            keySpec = RSAPrivateKeySpec(MODULUS, PRIVATE_EXP)
            privateKey = keyFactory.generatePrivate(keySpec)

            keyPair = KeyPair(publicKey, privateKey)

            if (spec != null) {
                x509Cert = FakeCertificateFactory.create(publicKey, spec)
                cert = CertificateNormalizer.toCertificate(x509Cert.encoded)
                keyStore.setKeyEntry(spec?.alias, keyPair.private, null,
                        arrayOf(cert))
            }
        } finally {
            spec = null
        }

        return keyPair
    }

    companion object {
        val PUBLIC_ENCODED: ByteArray = byteArrayOf(48, 92, 48, 13,
                6, 9, 42, -122, 72, -122, -9, 13, 1, 1, 1, 5, 0, 3,
                75, 0, 48, 72, 2, 64, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
                3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
                3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
                3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
                3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 4, 5, 5, 5, 5)
        val PRIVATE_ENCODED: ByteArray = byteArrayOf(
                48, -127, -79, 2, 1, 0, 48, 13, 6, 9, 42, -122, 72, -122, -9,
                13, 1, 1, 1, 5, 0, 4, -127, -100, 48, -127, -103, 2, 1, 0,
                2, 64, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
                3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
                3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
                3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
                3, 3, 3, 3, 3, 3, 2, 1, 0, 2, 64, 7, 7, 7, 7,
                7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
                7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
                7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
                7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
                2, 1, 0, 2, 1, 0, 2, 1, 0, 2, 1, 0, 2, 1, 0)
    }
}

class FakeKeyPairGeneratorSpec(
        val alias: String,
        val subject: X500Principal,
        val serialNumber: Long,
        val startDate: Date,
        val endDate: Date
) : AlgorithmParameterSpec
