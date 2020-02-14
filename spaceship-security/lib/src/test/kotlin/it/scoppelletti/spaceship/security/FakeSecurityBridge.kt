@file:Suppress("JoinDeclarationAndAssignment")

package it.scoppelletti.spaceship.security

import org.threeten.bp.Clock
import org.threeten.bp.ZonedDateTime
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.spec.AlgorithmParameterSpec
import java.util.Date
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.IvParameterSpec
import javax.security.auth.x500.X500Principal

private const val YEAR_MAX = 9999

internal class FakeSecurityBridge(
        private val clock: Clock
) : SecurityBridge {

    private val keyStore: KeyStore

    init {
        keyStore = FakeKeyStore(SecurityExtTest.KEYSTORE_TYPE) {
            Date(ZonedDateTime.now(clock).toEpochSecond() * 1000L)
        }.apply {
            load(null)
        }
    }

    override fun createCipher(transformation: String): Cipher =
            FakeCipher(transformation)

    override fun createCipher(
            transformation: String,
            provider: String
    ): Cipher = FakeCipher(transformation)

    override fun createCipherParameterSpec(
            iv: ByteArray
    ): AlgorithmParameterSpec = IvParameterSpec(iv)

    override fun createKeyGenerator(
            algorithm: String,
            provider: String
    ): KeyGenerator =
            StubKeyGenerator(SecurityExtTest.KEY_ALGORITHM_DES, keyStore)

    override fun createKeyGenParameterSpec(
            keystoreAlias: String,
            expire: Int
    ): AlgorithmParameterSpec {
        val startDate: ZonedDateTime
        val endDate: ZonedDateTime?

        startDate = ZonedDateTime.now(clock)
        endDate = if (expire > 0) startDate.plusDays(expire.toLong()) else null

        // - Android SDK 27.3
        // Methods of the class KeyGenParameterSpec.Builder are not mocked.
        return FakeKeyGenParameterSpec(keystoreAlias,
                Date(startDate.toEpochSecond() * 1000L),
                if (endDate == null) null else
                    Date(endDate.toEpochSecond() * 1000L))
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
        val startDate: ZonedDateTime
        val endDate: ZonedDateTime
        val subject: X500Principal

        subject = X500Principal(SecurityExt.TAG_CN + alias)
        serialNumber = System.currentTimeMillis()

        startDate = ZonedDateTime.now(clock)
        endDate = if (expire > 0) startDate.plusDays(expire.toLong()) else
            // Simulate no expiration
            startDate.withYear(YEAR_MAX)

        // - Android SDK 27.3
        // The class KeyGenPairParameterSpec.Builder needs the Context
        // object and I don't know I should mock that; then I suspect that
        // the methods of the class KeyGenPairParameterSpec.Builder are not
        // mocked just like the methods of the class
        // KeyGenParameterSpec.Builder.
        return FakeKeyPairGeneratorSpec(alias, subject, serialNumber,
                Date(startDate.toEpochSecond() * 1000L),
                Date(endDate.toEpochSecond() * 1000L))
    }

    override fun createKeyStore(type: String): KeyStore = keyStore
}
