package it.scoppelletti.spaceship.security

import it.scoppelletti.spaceship.io.closeQuietly
import it.scoppelletti.spaceship.time.FakeTimeProvider
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

private const val EXPIRE: Int = 30
private const val ALG_AES: String = "AES"
private const val ALIAS_KEY: String = "key"
private const val ALIAS_KEYPAIR: String = "keypair"
private val DATA: ByteArray = ByteArray(16) { _ -> 13}

class CipherProviderTest {

    private lateinit var cipherProvider: CipherProvider
    private lateinit var random: SecureRandom

    @BeforeTest
    fun before() {
        cipherProvider = FakeCipherProvider(FakeTimeProvider())
        random = StubSecureRandom.create()
    }

    @Test
    fun keyGenerator() {
        val key: Key
        val secretKey: SecretKey
        val keyStore: KeyStore

        secretKey = getSecretKey()
        assertTrue(secretKey.encoded contentEquals StubKeyGenerator.ENCODED,
                "Generated key is not well-known.")

        keyStore = cipherProvider.createKeyStore(SecurityExtTest.KEYSTORE_TYPE)
        key = keyStore.getKey(ALIAS_KEY, null)
        assertTrue(key is SecretKey, "Read key is not a SecretKey.")

        assertTrue(key.encoded contentEquals secretKey.encoded,
                "Read key is not identical to the generated key.")
    }

    private fun getSecretKey(): SecretKey {
        val keyGen: KeyGenerator
        val params: AlgorithmParameterSpec

        params = cipherProvider.createKeyGenParameterSpec(ALIAS_KEY, EXPIRE)

        keyGen = cipherProvider.createKeyGenerator(ALG_AES,
                SecurityExtTest.KEYSTORE_TYPE)
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

        params = cipherProvider.createKeyPairGenParameterSpec(ALIAS_KEYPAIR,
                EXPIRE)

        keyPairGen = cipherProvider.createKeyPairGenerator(
                SecurityExtTest.ALG_RSA, SecurityExtTest.PROVIDER)
                .apply {
                    initialize(params, random)
                }

        keyPair = keyPairGen.generateKeyPair()

        assertTrue(keyPair.public.encoded contentEquals
                StubKeyPairGenerator.PUBLIC_ENCODED,
                "Generated public key is not well-known.")
        assertTrue(keyPair.private.encoded contentEquals
                StubKeyPairGenerator.PRIVATE_ENCODED,
                "Generated private key is not well-known")

        keyStore = cipherProvider.createKeyStore(SecurityExtTest.KEYSTORE_TYPE)
        key = keyStore.getKey(ALIAS_KEYPAIR, null)
        assertTrue(key is PrivateKey, "Read key is not a PrivateKey.")

        assertTrue(key.encoded contentEquals keyPair.private.encoded,
                "Read key is not identical to the generated private key.")
    }

    @Test
    fun encrypt() {
        val cipher: Cipher
        val outputStream: ByteArrayOutputStream
        val encryptor: OutputStream

        cipher = cipherProvider.createCipher(ALG_AES)
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())

        outputStream = ByteArrayOutputStream()
        encryptor = CipherOutputStream(outputStream, cipher)
        encryptor.write(DATA)
        encryptor.closeQuietly()

        assertTrue(outputStream.toByteArray() contentEquals DATA)
    }

    @Test
    fun decrypt() {
        val cipher: Cipher
        val outputStream: ByteArrayOutputStream
        val decryptor: OutputStream

        cipher = cipherProvider.createCipher(ALG_AES)
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())

        outputStream = ByteArrayOutputStream()
        decryptor = CipherOutputStream(outputStream, cipher)
        decryptor.write(DATA)
        decryptor.closeQuietly()

        assertTrue(outputStream.toByteArray() contentEquals DATA)
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