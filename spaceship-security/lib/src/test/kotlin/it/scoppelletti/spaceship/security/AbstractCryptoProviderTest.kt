package it.scoppelletti.spaceship.security

import android.content.res.Resources
import io.reactivex.Single
import io.reactivex.SingleEmitter
import it.scoppelletti.spaceship.io.FakeIOProvider
import it.scoppelletti.spaceship.io.IOProvider
import it.scoppelletti.spaceship.io.closeQuietly
import it.scoppelletti.spaceship.time.FakeTimeProvider
import org.mockito.Mock
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.OutputStream
import java.security.GeneralSecurityException
import java.security.SecureRandom
import java.util.Calendar
import javax.crypto.Cipher
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

private const val EXPIRE: Int = 30
private const val ALIAS_KEY: String = "key"
private val DATA: ByteArray = ByteArray(16) { 13 }

abstract class AbstractCipherFactoryTest {

    @Mock
    lateinit var resources: Resources

    internal lateinit var cryptoProvider: CryptoProvider
    internal lateinit var securityBridge: SecurityBridge
    internal lateinit var timeProvider: FakeTimeProvider
    internal lateinit var random: SecureRandom

    fun onSetUp() {
        timeProvider = FakeTimeProvider()
        random = StubSecureRandom.create()
        securityBridge = FakeSecurityBridge(timeProvider)
    }

    fun onValidTest() {
        var decrypted: ByteArray? = null
        var err: Throwable? = null

        encrypt()
                .flatMap { data ->
                    decrypt(data)
                }
                .subscribe( { data ->
                    decrypted = data
                }, { ex ->
                    err = ex
                })

        if (err != null) {
            throw err!!
        }

        assertNotNull(decrypted, "No data found.")
        assertTrue(decrypted!! contentEquals DATA,
                "Decrypted object differs from encrypting object.")
    }

    fun onExpiredTest() {
        val currentTime: Calendar
        var decrypted: ByteArray? = null
        var err: Throwable? = null

        currentTime = timeProvider.currentTime()

        encrypt()
                .flatMap { data ->
                    decrypt(data)
                            .doOnSubscribe {
                                timeProvider.setCurrentTime(
                                        currentTime.apply {
                                            add(Calendar.DATE, EXPIRE + 10)
                                        }
                                )
                            }
                }
                .subscribe( { data ->
                    decrypted = data
                }, { ex ->
                    err = ex
                })

        assertFailsWith(GeneralSecurityException::class) {
            if (err != null) {
                throw err!!
            }
        }

        assertNull(decrypted, "Data should be expired.")
    }

    private fun encrypt(): Single<ByteArray> =
            cryptoProvider.newSecretKey(ALIAS_KEY, EXPIRE)
                    .flatMap { key ->
                        cryptoProvider.newEncryptor(key)
                    }
                    .flatMap { cipher ->
                        Single.create<ByteArray> { emitter ->
                            onEncryptSubscribe(emitter, cipher)
                        }
                    }

    private fun onEncryptSubscribe(
            emitter: SingleEmitter<ByteArray>,
            cipher: Cipher) {
        val outputStream: ByteArrayOutputStream
        val objectStream: ObjectOutputStream
        val encryptor: OutputStream

        outputStream = ByteArrayOutputStream()
        objectStream = ObjectOutputStream(outputStream)
        objectStream.writeInt(cipher.iv.size)
        objectStream.flush()

        outputStream.write(cipher.iv)
        outputStream.flush()

        encryptor = cryptoProvider.cipherOutputStream(
                outputStream, cipher)
        encryptor.write(DATA)
        encryptor.closeQuietly()

        emitter.onSuccess(outputStream.toByteArray())
    }

    private fun decrypt(data: ByteArray): Single<ByteArray> =
            cryptoProvider.loadSecretKey(ALIAS_KEY)
                    .flatMap { key ->
                        Single.create<ByteArray> { emitter ->
                            onLoadIVSubscribe(emitter, data)
                        }.map { iv ->
                            Pair(key, iv)
                        }
                    }
                    .flatMap { (key, iv) ->
                        cryptoProvider.newDecryptor(key, iv)
                    }
                    .flatMap { cipher ->
                        Single.create<ByteArray>{ emitter ->
                            onDecryptSubscribe(emitter, data, cipher)
                        }
                    }

    private fun onLoadIVSubscribe(
            emitter: SingleEmitter<ByteArray>,
            data: ByteArray
    ) {
        val ivSize: Int
        val iv: ByteArray
        val inputStream: ByteArrayInputStream
        val objectStream: ObjectInputStream

        inputStream = ByteArrayInputStream(data)

        objectStream = ObjectInputStream(inputStream)
        ivSize = objectStream.readInt()

        iv = ByteArray(ivSize)
        if (inputStream.read(iv, 0, ivSize) != ivSize) {
            emitter.tryOnError(IOException("Data corrupted"))
            return
        }

        emitter.onSuccess(iv)
    }

    private fun onDecryptSubscribe(
            emitter: SingleEmitter<ByteArray>,
            data: ByteArray,
            cipher: Cipher
    ) {
        val ivSize: Long
        val buf: ByteArray
        val inputStream: ByteArrayInputStream
        val objectStream: ObjectInputStream
        val decryptor: InputStream
        val outputStream: ByteArrayOutputStream
        var n: Int

        inputStream = ByteArrayInputStream(data)
        objectStream = ObjectInputStream(inputStream)
        ivSize = objectStream.readInt().toLong()

        if (inputStream.skip(ivSize) != ivSize) {
            emitter.tryOnError(IOException("Data corrupted"))
            return
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
        emitter.onSuccess(outputStream.toByteArray())
    }
}


