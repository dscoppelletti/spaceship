package it.scoppelletti.spaceship.security.sample.lifecycle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.scoppelletti.spaceship.ApplicationException
import it.scoppelletti.spaceship.StdlibExt
import it.scoppelletti.spaceship.io.IOProvider
import it.scoppelletti.spaceship.io.closeQuietly
import it.scoppelletti.spaceship.security.CryptoProvider
import it.scoppelletti.spaceship.security.sample.i18n.SampleMessages
import it.scoppelletti.spaceship.types.StringExt
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.OutputStream
import java.lang.Exception
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.inject.Inject
import javax.inject.Named

@Suppress("BlockingMethodInNonBlockingContext")
class CipherViewModel @Inject constructor(
        private val ioProvider: IOProvider,
        private val cryptoProvider: CryptoProvider,
        private val sampleMessages: SampleMessages,

        @Named(StdlibExt.DEP_MAINDISPATCHER)
        dispatcher: CoroutineDispatcher
): ViewModel() {

    private val scope = CoroutineScope(dispatcher + Job())
    private val _state: MutableLiveData<MainState> = MutableLiveData()
    private val _form: CipherForm = CipherForm()

    init {
        _state.value = MainState.create()
    }

    val state: LiveData<MainState> = _state
    val form: CipherForm = _form

    fun encrypt(alias: String, plainText: String) = scope.launch {
        val key: SecretKey
        val cipher: Cipher

        try {
            _state.value = _state.value?.withWaiting()
            _form.cipherText = StringExt.EMPTY

            key = cryptoProvider.loadSecretKey(alias)
            if (!isActive) {
                throw CancellationException()
            }

            cipher = cryptoProvider.newEncryptor(key)
            if (!isActive) {
                throw CancellationException()
            }

            _form.cipherText = encrypt(plainText, cipher)
            _state.value = MainState.create()
        } catch (ex: CancellationException) {
            throw ex
        } catch (ex: Exception) {
            _state.value = _state.value?.withError(ex)
        }
    }

    private suspend fun encrypt(plainText: String, cipher: Cipher): String =
            withContext(Dispatchers.Default) {
                var outputStream: ByteArrayOutputStream? = null
                var objectStream: ObjectOutputStream? = null
                var encoder: OutputStream? = null
                var encryptor: OutputStream? = null

                try {
                    outputStream = ByteArrayOutputStream()
                    encoder = ioProvider.base64OutputStream(outputStream)

                    objectStream = ObjectOutputStream(encoder)
                    objectStream.writeInt(cipher.iv.size)
                    objectStream.flush()

                    encoder.write(cipher.iv)
                    encoder.flush()

                    encryptor = cryptoProvider.cipherOutputStream(encoder,
                            cipher)
                    encoder = null
                    objectStream = null // Don't close this! Leave closing
                        // encoder to the encryptor

                    encryptor.write(plainText.toByteArray())
                    encryptor.flush()
                } finally {
                    outputStream?.closeQuietly()
                    objectStream?.closeQuietly()
                    encoder?.closeQuietly()
                    encryptor?.closeQuietly()
                }

                outputStream.toString()
            }

    fun decrypt(alias: String, cipherText: String) = scope.launch {
        val key: SecretKey
        val cipher: Cipher
        val iv: ByteArray

        try {
            _state.value = _state.value?.withWaiting()
            _form.plainText = StringExt.EMPTY

            key = cryptoProvider.loadSecretKey(alias)
            iv = loadIV(cipherText)
            cipher = cryptoProvider.newDecryptor(key, iv)

            form.plainText = decrypt(cipherText, cipher)
            _state.value = MainState.create()
        } catch (ex: CancellationException) {
            throw ex
        } catch (ex: Exception) {
            _state.value = _state.value?.withError(ex)
        }
    }

    private suspend fun loadIV(cipherText: String): ByteArray =
            withContext(Dispatchers.Default) {
                val ivSize: Int
                val iv: ByteArray
                var inputStream: ByteArrayInputStream? = null
                var decoder: InputStream? = null
                var objectStream: ObjectInputStream? = null

                try {
                    inputStream = ByteArrayInputStream(cipherText.toByteArray())
                    decoder = ioProvider.base64InputStream(inputStream)
                    inputStream = null

                    objectStream = ObjectInputStream(decoder)

                    ivSize = objectStream.readInt()
                    iv = ByteArray(ivSize)
                    if (decoder.read(iv, 0, ivSize) != ivSize) {
                        throw ApplicationException(
                                sampleMessages.errorCipherTextCorrupted())
                    }
                } finally {
                    inputStream?.closeQuietly()
                    objectStream?.closeQuietly()
                    decoder?.closeQuietly()
                    objectStream?.closeQuietly()
                }

                iv
            }

    private suspend fun decrypt(cipherText: String, cipher: Cipher): String =
            withContext(Dispatchers.Default) {
                val ivSize: Long
                val buf: ByteArray
                var n: Int
                var inputStream: ByteArrayInputStream? = null
                var decoder: InputStream? = null
                var objectStream: ObjectInputStream? = null
                var decryptor: InputStream? = null
                var outputStream: ByteArrayOutputStream? = null

                try {
                    inputStream = ByteArrayInputStream(cipherText.toByteArray())
                    decoder = ioProvider.base64InputStream(inputStream)
                    inputStream = null

                    objectStream = ObjectInputStream(decoder)

                    ivSize = objectStream.readInt().toLong()
                    if (decoder.skip(ivSize) != ivSize) {
                        throw ApplicationException(
                                sampleMessages.errorCipherTextCorrupted())
                    }

                    if (!isActive) {
                        throw CancellationException()
                    }

                    decryptor = cryptoProvider.cipherInputStream(decoder, cipher)
                    outputStream = ByteArrayOutputStream()
                    buf = ByteArray(DEFAULT_BUFFER_SIZE)

                    n = decryptor.read(buf, 0, DEFAULT_BUFFER_SIZE)
                    while (n > 0) {
                        if (!isActive) {
                            throw CancellationException()
                        }

                        outputStream.write(buf, 0, n)
                        if (!isActive) {
                            throw CancellationException()
                        }

                        n = decryptor.read(buf, 0, DEFAULT_BUFFER_SIZE)
                    }
                } finally {
                    inputStream?.closeQuietly()
                    objectStream?.closeQuietly()
                    decoder?.closeQuietly()
                    objectStream?.closeQuietly()
                    decryptor?.closeQuietly()
                    outputStream?.closeQuietly()
                }

                outputStream.toString()
            }

    fun setError(ex: Throwable) {
        _state.value = _state.value?.withError(ex)
    }

    override fun onCleared() {
        scope.cancel()
        super.onCleared()
    }
}

