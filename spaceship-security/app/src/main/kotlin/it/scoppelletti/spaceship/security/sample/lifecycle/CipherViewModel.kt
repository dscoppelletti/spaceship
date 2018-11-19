package it.scoppelletti.spaceship.security.sample.lifecycle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import it.scoppelletti.spaceship.applicationException
import it.scoppelletti.spaceship.io.IOProvider
import it.scoppelletti.spaceship.io.closeQuietly
import it.scoppelletti.spaceship.security.CryptoProvider
import it.scoppelletti.spaceship.security.sample.R
import it.scoppelletti.spaceship.types.StringExt
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.OutputStream
import java.lang.Exception
import javax.crypto.Cipher
import javax.inject.Inject

class CipherViewModel @Inject constructor(
        private val ioProvider: IOProvider,
        private val cryptoProvider: CryptoProvider
): ViewModel() {
    private val _state: MutableLiveData<MainState>
    private val _form: CipherForm
    private val disposables: CompositeDisposable

    val state: LiveData<MainState>
        get() = _state

    val form: CipherForm
        get() = _form

    init {
        _state = MutableLiveData()
        _state.value = MainState.create()
        _form = CipherForm()
        disposables = CompositeDisposable()
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }

    fun setError(ex: Throwable) {
        _state.value = _state.value?.withError(ex)
    }

    fun encrypt(alias: String, plainText: String) {
        val subscription: Disposable

        _state.value = _state.value?.withWaiting()
        _form.cipherText = StringExt.EMPTY
        subscription = cryptoProvider.loadSecretKey(alias)
                .flatMap { key ->
                    cryptoProvider.newEncryptor(key)
                }
                .flatMap { cipher ->
                    Single.create<String> { emitter ->
                        onEncryptSubscribe(emitter, plainText, cipher)
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    _form.cipherText = result
                    _state.value = MainState.create()
                }, { ex ->
                    _state.value = _state.value?.withError(ex)
                })
        disposables.add(subscription)
    }

    private fun onEncryptSubscribe(
            emitter: SingleEmitter<String>,
            plainText: String,
            cipher: Cipher) {
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

            encryptor = cryptoProvider.cipherOutputStream(encoder, cipher)
            encoder = null
            objectStream = null // Don't close this! Leave closing encoder to
                // the encryptor

            encryptor.write(plainText.toByteArray())
            encryptor.flush()
        } catch (ex: Exception) {
            emitter.tryOnError(ex)
            return
        } finally {
            outputStream?.closeQuietly()
            objectStream?.closeQuietly()
            encoder?.closeQuietly()
            encryptor?.closeQuietly()
        }

        emitter.onSuccess(outputStream.toString())
    }

    fun decrypt(alias: String, plainText: String) {
        val subscription: Disposable

        _state.value = _state.value?.withWaiting()
        _form.plainText = StringExt.EMPTY
        subscription = cryptoProvider.loadSecretKey(alias)
                .flatMap { key ->
                    Single.create<ByteArray> { emitter ->
                        onLoadIVSubscribe(emitter, plainText)
                    }.flatMap { iv ->
                        cryptoProvider.newDecryptor(key, iv)
                    }
                }
                .flatMap { cipher ->
                    Single.create<String> { emitter ->
                        onDecryptSubscribe(emitter, plainText, cipher)
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    _form.plainText = result
                    _state.value = MainState.create()
                }, { ex ->
                    _state.value = _state.value?.withError(ex)
                })
        disposables.add(subscription)
    }

    private fun onLoadIVSubscribe(
            emitter: SingleEmitter<ByteArray>,
            cipherText: String
    ) {
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
                throw applicationException {
                    message(R.string.err_ciphertext_corrupted)
                }
            }
        } catch (ex: Exception) {
            emitter.tryOnError(ex)
            return
        } finally {
            inputStream?.closeQuietly()
            objectStream?.closeQuietly()
            decoder?.closeQuietly()
            objectStream?.closeQuietly()
        }

        emitter.onSuccess(iv)
    }

    private fun onDecryptSubscribe(
            emitter: SingleEmitter<String>,
            cipherText: String,
            cipher: Cipher
    ) {
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
                throw applicationException {
                    message(R.string.err_ciphertext_corrupted)
                }
            }

            if (emitter.isDisposed) {
                return
            }

            decryptor = cryptoProvider.cipherInputStream(decoder, cipher)
            outputStream = ByteArrayOutputStream()
            buf = ByteArray(DEFAULT_BUFFER_SIZE)

            n = decryptor.read(buf, 0, DEFAULT_BUFFER_SIZE)
            while (n > 0) {
                if (emitter.isDisposed) {
                    return
                }

                outputStream.write(buf, 0, n)
                if (emitter.isDisposed) {
                    return
                }

                n = decryptor.read(buf, 0, DEFAULT_BUFFER_SIZE)
            }
        } catch (ex: Exception) {
            emitter.tryOnError(ex)
            return
        } finally {
            inputStream?.closeQuietly()
            objectStream?.closeQuietly()
            decoder?.closeQuietly()
            objectStream?.closeQuietly()
            decryptor?.closeQuietly()
            outputStream?.closeQuietly()
        }

        emitter.onSuccess(outputStream.toString())
    }
}

