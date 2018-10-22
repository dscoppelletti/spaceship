package it.scoppelletti.spaceship.security

import android.content.res.Resources
import io.reactivex.Single
import it.scoppelletti.spaceship.io.FakeIOProvider
import it.scoppelletti.spaceship.io.IOProvider
import it.scoppelletti.spaceship.io.closeQuietly
import it.scoppelletti.spaceship.time.FakeTimeProvider
import org.mockito.Mock
import org.mockito.Mockito
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.security.GeneralSecurityException
import java.security.SecureRandom
import java.util.Calendar
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

private const val EXPIRE: Int = 30
private const val ALIAS_KEY: String = "key"
private val DATA: ByteArray = ByteArray(16) { _ -> 13}

abstract class AbstractCipherFactoryTest {

    @Mock
    lateinit var resources: Resources

    internal lateinit var cipherFactory: CipherFactory
    internal lateinit var cipherProvider: CipherProvider
    internal lateinit var ioProvider: IOProvider
    internal lateinit var timeProvider: FakeTimeProvider
    internal lateinit var random: SecureRandom

    fun onBefore() {
        Mockito.`when`(resources.getString(
                R.string.it_scoppelletti_security_err_aliasInvalid))
                .thenReturn("Invalid alias %1\$s.")

        Mockito.`when`(resources.getString(
                R.string.it_scoppelletti_security_err_aliasNotFound))
                .thenReturn("Decryption key %1\$s not found.")

        timeProvider = FakeTimeProvider()
        ioProvider = FakeIOProvider()
        random = StubSecureRandom.create()
        cipherProvider = FakeCipherProvider(timeProvider)
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
        cipherFactory.newEncryptor(ALIAS_KEY, EXPIRE)
                .map { cipher ->
                    val outputStream: ByteArrayOutputStream
                    val encryptor: OutputStream

                    outputStream = ByteArrayOutputStream()
                    encryptor = cipherFactory.cipherOutputStream(outputStream,
                            cipher)
                    encryptor.write(DATA)
                    encryptor.closeQuietly()

                    outputStream.toByteArray()
                }

    private fun decrypt(data: ByteArray): Single<ByteArray> =
            cipherFactory.newDecryptor(ALIAS_KEY)
                    .map { cipher ->
                        val outputStream: ByteArrayOutputStream
                        val decryptor: OutputStream

                        outputStream = ByteArrayOutputStream()
                        decryptor = cipherFactory.cipherOutputStream(
                                outputStream, cipher)
                        decryptor.write(data)
                        decryptor.closeQuietly()

                        outputStream.toByteArray()
                    }
}



