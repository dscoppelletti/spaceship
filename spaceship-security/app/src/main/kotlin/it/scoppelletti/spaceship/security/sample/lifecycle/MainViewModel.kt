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
import it.scoppelletti.spaceship.io.IOProvider
import it.scoppelletti.spaceship.lifecycle.SingleEvent
import it.scoppelletti.spaceship.security.CipherFactory
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception
import javax.crypto.Cipher
import javax.inject.Inject

class MainViewModel @Inject constructor(
        private val cipherFactory: CipherFactory,
        private val ioProvider: IOProvider
): ViewModel() {
    private val _state: MutableLiveData<SingleEvent<Throwable>>
    private val _form: MainForm
    private val disposables: CompositeDisposable

    val state: LiveData<SingleEvent<Throwable>>
        get() = _state

    val form: MainForm
        get() = _form

    init {
        _state = MutableLiveData()
        _form = MainForm()
        disposables = CompositeDisposable()
    }

    fun encrypt(alias: String, expire: Int, clearText: String) {
        val subcription: Disposable

        subcription = cipherFactory.newEncryptor(alias, expire)
                .flatMap { cipher ->
                    Single.create<String> { emitter ->
                        onEncryptSubscribe(emitter, clearText, cipher)
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    _form.encryptedText = result
                }, { ex ->
                    _state.value = SingleEvent(ex)
                })
        disposables.add(subcription)
    }

    private fun onEncryptSubscribe(
            emitter: SingleEmitter<String>,
            clearText: String,
            cipher: Cipher
    ) {
        val outputStream: ByteArrayOutputStream
        val encoder: OutputStream
        val encryptor: OutputStream
        val data: ByteArray

        try {
            outputStream = ByteArrayOutputStream()
            encoder = ioProvider.base64OutputStream(outputStream)
            encryptor = cipherFactory.cipherOutputStream(encoder, cipher)

            data = clearText.toByteArray()
            encryptor.write(data, 0, data.size)
            encryptor.close()
        } catch (ex: Exception) {
            emitter.tryOnError(ex)
            return
        }

        emitter.onSuccess(outputStream.toString())
    }

    fun decrypt(alias: String, encryptedText: String) {
        val subcription: Disposable

        subcription = cipherFactory.newDecryptor(alias)
                .flatMap{ cipher ->
                    Single.create<String> { emitter ->
                        onDecryptSubscribe(emitter, encryptedText, cipher)
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    _form.clearText = result
                }, { ex ->
                    _state.value = SingleEvent(ex)
                })
        disposables.add(subcription)
    }

    private fun onDecryptSubscribe(
            emitter: SingleEmitter<String>,
            encryptedText: String,
            cipher: Cipher
    ) {
        var n: Int
        val inputStream: InputStream
        val decoder: InputStream
        val decryptor: InputStream
        val outputStream: ByteArrayOutputStream
        val buf: ByteArray

        try {
            inputStream = ByteArrayInputStream(encryptedText.toByteArray())
            decoder = ioProvider.base64InputStream(inputStream)
            decryptor = cipherFactory.cipherInputStream(decoder, cipher)
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

            decryptor.close()
            outputStream.close()
        } catch (ex: Exception) {
            emitter.tryOnError(ex)
            return
        }

        emitter.onSuccess(outputStream.toString())
    }
}
