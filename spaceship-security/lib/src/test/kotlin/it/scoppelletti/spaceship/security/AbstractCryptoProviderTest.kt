@file:Suppress("JoinDeclarationAndAssignment")

package it.scoppelletti.spaceship.security

import it.scoppelletti.spaceship.io.closeQuietly
import it.scoppelletti.spaceship.types.FakeClock
import kotlinx.coroutines.runBlocking
import org.threeten.bp.Clock
import org.threeten.bp.ZonedDateTime
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.OutputStream
import java.security.GeneralSecurityException
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

private const val EXPIRE = 30
private const val ALIAS_KEY = "key"
private val DATA = ByteArray(16) { 13 }

abstract class AbstractCryptoProviderTest {

    internal lateinit var cryptoProvider: CryptoProvider
    internal lateinit var securityBridge: SecurityBridge
    internal lateinit var random: SecureRandom
    internal lateinit var clock: FakeClock

    protected fun onSetUp() {
        random = StubSecureRandom.create()
        clock = FakeClock(Clock.systemUTC())
        securityBridge = FakeSecurityBridge(clock)
    }

    protected fun onNoExpirationTest() {
        val encrypted: ByteArray
        val decrypted: ByteArray

        encrypted = encrypt(0)
        decrypted = decrypt(encrypted)

        assertTrue(decrypted contentEquals DATA,
                "Decrypted object differs from encrypting object.")
    }

    protected fun onValidTest() {
        val encrypted: ByteArray
        val decrypted: ByteArray

        encrypted = encrypt(EXPIRE)
        decrypted = decrypt(encrypted)

        assertTrue(decrypted contentEquals DATA,
                "Decrypted object differs from encrypting object.")
    }

    protected fun onExpiredTest() {
        val currentTime: ZonedDateTime
        val encrypted: ByteArray

        currentTime = ZonedDateTime.now(clock)
        encrypted = encrypt(EXPIRE)

        clock.impl = Clock.fixed(currentTime.plusDays(EXPIRE + 10L).toInstant(),
                currentTime.zone)

        assertFailsWith(GeneralSecurityException::class) {
            decrypt(encrypted)
        }
    }

    private fun encrypt(expire: Int): ByteArray = runBlocking {
        val key: SecretKey
        val cipher: Cipher

        key = cryptoProvider.newSecretKey(ALIAS_KEY, expire)
        cipher = cryptoProvider.newEncryptor(key)
        encrypt(cipher)
    }

    private fun encrypt(cipher: Cipher): ByteArray {
        val outputStream: ByteArrayOutputStream
        val objectStream: ObjectOutputStream
        val encryptor: OutputStream

        outputStream = ByteArrayOutputStream()
        objectStream = ObjectOutputStream(outputStream)
        objectStream.writeInt(cipher.iv.size)
        objectStream.flush()

        outputStream.write(cipher.iv)
        outputStream.flush()

        encryptor = cryptoProvider.cipherOutputStream(outputStream, cipher)
        encryptor.write(DATA)
        encryptor.closeQuietly()

        return outputStream.toByteArray()
    }

    private fun decrypt(data: ByteArray): ByteArray = runBlocking {
        val key: SecretKey
        val cipher: Cipher
        val iv: ByteArray

        key = cryptoProvider.loadSecretKey(ALIAS_KEY)
        iv = loadIV(data)
        cipher = cryptoProvider.newDecryptor(key, iv)
        decrypt(data, cipher)
    }

    private fun loadIV(data: ByteArray): ByteArray {
        val ivSize: Int
        val iv: ByteArray
        val inputStream: InputStream
        val objectStream: ObjectInputStream

        inputStream = ByteArrayInputStream(data)
        objectStream = ObjectInputStream(inputStream)
        ivSize = objectStream.readInt()

        iv = ByteArray(ivSize)
        if (inputStream.read(iv, 0, ivSize) != ivSize) {
            throw IOException("Data corrupted")
        }

        return iv
    }

    private fun decrypt(data: ByteArray, cipher: Cipher): ByteArray {
        val ivSize: Long
        val inputStream: InputStream
        val objectStream: ObjectInputStream
        val decryptor: InputStream
        val outputStream: ByteArrayOutputStream
        val buf: ByteArray
        var n: Int

        inputStream = ByteArrayInputStream(data)
        objectStream = ObjectInputStream(inputStream)
        ivSize = objectStream.readInt().toLong()

        if (inputStream.skip(ivSize) != ivSize) {
            throw IOException("Data corrupted")
        }

        decryptor = cryptoProvider.cipherInputStream(inputStream, cipher)
        outputStream = ByteArrayOutputStream()
        buf = ByteArray(DEFAULT_BUFFER_SIZE)

        n = decryptor.read(buf, 0, DEFAULT_BUFFER_SIZE)
        while (n > 0) {
            outputStream.write(buf, 0, n)
            n = decryptor.read(buf, 0, DEFAULT_BUFFER_SIZE)
        }

        outputStream.closeQuietly()
        return outputStream.toByteArray()
    }
}


