@file:Suppress("JoinDeclarationAndAssignment")

package it.scoppelletti.spaceship.security

import it.scoppelletti.spaceship.io.closeQuietly
import org.threeten.bp.Clock
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.security.Key
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.SecureRandom
import java.security.Security
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.Cipher
import javax.crypto.CipherOutputStream
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

private const val ALIAS_KEY = "key"
private const val ALIAS_KEYPAIR = "keypair"
private val DATA = ByteArray(16) { 13 }

class SecuirtyBridgeNoExpirationTest {

    private lateinit var securityBridge: SecurityBridge
    private lateinit var random: SecureRandom

    @BeforeTest
    fun setUp() {
        securityBridge = FakeSecurityBridge(Clock.systemUTC())
        random = StubSecureRandom.create()
    }

    @Test
    fun encrypt() {
        val outputStream: ByteArrayOutputStream
        val encryptor: OutputStream
        val cipher: Cipher

        cipher = securityBridge.createCipher(SecurityExt.KEY_ALGORITHM_AES)
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())

        outputStream = ByteArrayOutputStream()
        encryptor = CipherOutputStream(outputStream, cipher)
        encryptor.write(DATA)
        encryptor.closeQuietly()

        assertTrue(outputStream.toByteArray() contentEquals DATA)
    }

    @Test
    fun decrypt() {
        val outputStream: ByteArrayOutputStream
        val decryptor: OutputStream
        val cipher: Cipher

        cipher = securityBridge.createCipher(SecurityExt.KEY_ALGORITHM_AES)
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())

        outputStream = ByteArrayOutputStream()
        decryptor = CipherOutputStream(outputStream, cipher)
        decryptor.write(DATA)
        decryptor.closeQuietly()

        assertTrue(outputStream.toByteArray() contentEquals DATA)
    }

    @Test
    fun keyGenerator() {
        val key: Key
        val secretKey: SecretKey
        val keyStore: KeyStore

        secretKey = getSecretKey()
        assertTrue(secretKey.encoded contentEquals StubKeyGenerator.ENCODED,
                "Generated key is not well-known.")

        keyStore = securityBridge.createKeyStore(
                SecurityExtTest.KEYSTORE_TYPE)
        key = keyStore.getKey(ALIAS_KEY, null)
        assertTrue(key is SecretKey, "Read key is not a SecretKey.")

        assertTrue(key.encoded contentEquals secretKey.encoded,
                "Read key is not identical to the generated key.")
    }

    private fun getSecretKey(): SecretKey {
        val keyGen: KeyGenerator
        val params: AlgorithmParameterSpec

        params = securityBridge.createKeyGenParameterSpec(ALIAS_KEY, 0)
        keyGen = securityBridge.createKeyGenerator(
                SecurityExt.KEY_ALGORITHM_AES, SecurityExtTest.KEYSTORE_TYPE)
                .apply {
                    init(params, random)
                }

        return keyGen.generateKey()
    }

    @Test
    fun keyPairGenerator() {
        val key: Key
        val keyPair: KeyPair
        val keyPairGen: KeyPairGenerator
        val params: AlgorithmParameterSpec
        val keyStore: KeyStore

        params = securityBridge.createKeyPairGenParameterSpec(ALIAS_KEYPAIR, 0)

        keyPairGen = securityBridge.createKeyPairGenerator(
                SecurityExt.KEY_ALGORITHM_RSA, SecurityExtTest.PROVIDER_SUN)
                .apply {
                    initialize(params, random)
                }

        keyPair = keyPairGen.generateKeyPair()

        assertTrue(keyPair.public.encoded contentEquals
                StubKeyPairGenerator.PUBLIC_ENCODED,
                "Generated public key is not well-known.")
        assertTrue(keyPair.private.encoded contentEquals
                StubKeyPairGenerator.PRIVATE_ENCODED,
                "Generated private key is not well-known.")

        keyStore = securityBridge.createKeyStore(
                SecurityExtTest.KEYSTORE_TYPE)
        key = keyStore.getKey(ALIAS_KEYPAIR, null)
        assertTrue(key is PrivateKey, "Read key is not a PrivateKey.")

        assertTrue(key.encoded contentEquals keyPair.private.encoded,
                "Read key is not identical to the generated private key.")
    }

    @Test
    fun providers() {
        Security.getProviders().forEach { provider ->
            println("Provider=${provider.name}")
            provider.services.forEach { service ->
                println("type=${service.type} alg=${service.algorithm}")
            }
        }
    }
}
