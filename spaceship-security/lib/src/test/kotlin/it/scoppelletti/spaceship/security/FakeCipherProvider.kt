package it.scoppelletti.spaceship.security

import it.scoppelletti.spaceship.types.TimeProvider
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.spec.AlgorithmParameterSpec
import java.util.Calendar
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.NullCipher
import javax.security.auth.x500.X500Principal

internal class FakeCipherProvider(
        private val timeProvider: TimeProvider
) : CipherProvider {

    private val keyStore: KeyStore

    init {
        keyStore = FakeKeyStore(SecurityExtTest.KEYSTORE_TYPE) {
            timeProvider.currentTime().time
        }.apply {
            load(null)
        }
    }

    override fun createKeyGenerator(
            algorithm: String,
            provider: String
    ): KeyGenerator =
    // - Android SDK 27.3
    // No provider provides the AES algorithm for SecretKeyFactory type.
            StubKeyGenerator(FakeCipherProvider.ALG_DES, keyStore)

    override fun createKeyGenParameterSpec(
            keystoreAlias: String,
            expire: Int
    ): AlgorithmParameterSpec {
        val startDate: Calendar
        val endDate: Calendar

        startDate = timeProvider.currentTime()
        endDate = startDate.clone() as Calendar
        endDate.add(Calendar.DATE, expire)

        // - Android SDK 27.3
        // Methods of the class KeyGenParameterSpec.Builder are not mocked.
        return FakeKeyGenParameterSpec(keystoreAlias, startDate.time,
                endDate.time)
    }

    override fun createKeyPairGenerator(
            algorithm: String,
            provider: String
    ): KeyPairGenerator = StubKeyPairGenerator(algorithm, keyStore)

    override fun createKeyPairGenParameterSpec(
            alias: String,
            expire: Int
    ): AlgorithmParameterSpec {
        val serialNumber: Long
        val startDate: Calendar
        val endDate: Calendar
        val subject: X500Principal

        subject = X500Principal(SecurityExt.TAG_CN + alias)
        serialNumber = System.currentTimeMillis()

        startDate = timeProvider.currentTime()
        endDate = startDate.clone() as Calendar
        endDate.add(Calendar.DATE, expire)

        // - Android SDK 27.3
        // The class KeyGenPairParameterSpec.Builder needs the Context
        // object and I don't know I should mock that; then I suspect that
        // the methods of the class KeyGenPairParameterSpec.Builder are not
        // mocked just like the methods of the class
        // KeyGenParameterSpec.Builder.
        return FakeKeyPairGeneratorSpec(alias, subject, serialNumber,
                startDate.time, endDate.time)
    }

    override fun createCipher(transformation: String): Cipher = NullCipher()

    override fun createCipher(
            transformation: String,
            provider: String
    ): Cipher = NullCipher()

    override fun createKeyStore(type: String): KeyStore = keyStore

    private companion object {
        const val ALG_DES: String = "DES"
    }
}
