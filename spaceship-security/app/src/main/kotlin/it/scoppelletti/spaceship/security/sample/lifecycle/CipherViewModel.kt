@file:Suppress("JoinDeclarationAndAssignment")

package it.scoppelletti.spaceship.security.sample.lifecycle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.scoppelletti.spaceship.StdlibExt
import it.scoppelletti.spaceship.io.closeQuietly
import it.scoppelletti.spaceship.security.CryptoProvider
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
import okio.BufferedSink
import okio.BufferedSource
import okio.ByteString
import okio.Okio
import okio.Sink
import okio.Source
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.inject.Inject
import javax.inject.Named

@Suppress("BlockingMethodInNonBlockingContext")
class CipherViewModel @Inject constructor(
        private val cryptoProvider: CryptoProvider,

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
                val buf: ByteString
                val data: ByteArray
                val sink: Sink
                val bufSink: BufferedSink
                val encryptor: OutputStream
                val outputStream: ByteArrayOutputStream

                outputStream = ByteArrayOutputStream()
                sink = Okio.sink(outputStream)
                bufSink = Okio.buffer(sink)

                bufSink.writeInt(cipher.iv.size)
                bufSink.write(cipher.iv)
                bufSink.flush()

                if (!isActive) {
                    throw CancellationException()
                }

                encryptor = cryptoProvider.cipherOutputStream(
                        bufSink.outputStream(), cipher)
                encryptor.write(plainText.toByteArray())
                encryptor.closeQuietly()

                if (!isActive) {
                    throw CancellationException()
                }

                data = outputStream.toByteArray()
                buf = ByteString.of(data, 0, data.size)
                buf.base64()
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
                val inputStream: InputStream
                val source: Source
                val bufSource: BufferedSource
                val buf: ByteString?

                buf = ByteString.decodeBase64(cipherText)
                if (buf == null) {
                    throw IOException("No base64 data.")
                }

                if (!isActive) {
                    throw CancellationException()
                }

                inputStream = ByteArrayInputStream(buf.toByteArray())
                source = Okio.source(inputStream)
                bufSource = Okio.buffer(source)

                ivSize = bufSource.readInt()

                if (!isActive) {
                    throw CancellationException()
                }

                bufSource.readByteArray(ivSize.toLong())
            }

    private suspend fun decrypt(cipherText: String, cipher: Cipher): String =
            withContext(Dispatchers.Default) {
                val ivSize: Int
                val data: ByteArray
                val buf: ByteString?
                val decryptor: InputStream
                var source: Source
                var bufSource: BufferedSource
                var inputStream: InputStream

                buf = ByteString.decodeBase64(cipherText)
                if (buf == null) {
                    throw IOException("No base64 data.")
                }

                if (!isActive) {
                    throw CancellationException()
                }

                inputStream = ByteArrayInputStream(buf.toByteArray())
                source = Okio.source(inputStream)
                bufSource = Okio.buffer(source)

                ivSize = bufSource.readInt()
                bufSource.readByteArray(ivSize.toLong())

                if (!isActive) {
                    throw CancellationException()
                }

                data = bufSource.readByteArray()

                if (!isActive) {
                    throw CancellationException()
                }

                inputStream = ByteArrayInputStream(data)
                decryptor = cryptoProvider.cipherInputStream(inputStream,
                        cipher)
                source = Okio.source(decryptor)
                bufSource = Okio.buffer(source)

                bufSource.readUtf8()
            }

    fun setError(ex: Throwable) {
        _state.value = _state.value?.withError(ex)
    }

    override fun onCleared() {
        scope.cancel()
        super.onCleared()
    }
}

