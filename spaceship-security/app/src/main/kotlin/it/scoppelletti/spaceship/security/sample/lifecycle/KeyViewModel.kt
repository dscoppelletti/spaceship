package it.scoppelletti.spaceship.security.sample.lifecycle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import it.scoppelletti.spaceship.security.CryptoProvider
import it.scoppelletti.spaceship.security.sample.R
import javax.inject.Inject

class KeyViewModel @Inject constructor(
        private val cryptoProvider: CryptoProvider
): ViewModel() {
    private val _state: MutableLiveData<MainState>
    private val _form: KeyForm
    private val disposables: CompositeDisposable

    val state: LiveData<MainState>
        get() = _state

    val form: KeyForm
        get() = _form

    init {
        _state = MutableLiveData()
        _state.value = MainState.create()
        _form = KeyForm()
        disposables = CompositeDisposable()
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }

    fun setError(ex: Throwable) {
        _state.value = _state.value?.withError(ex)
    }

    fun createSecretKey(alias: String, expire: Int) {
        val subscription: Disposable

        _state.value = _state.value?.withWaiting()
        subscription = cryptoProvider.newSecretKey(alias, expire)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _state.value = MainState.create(R.string.msg_keyGenerated)
                }, { ex ->
                    _state.value = _state.value?.withError(ex)
                })
        disposables.add(subscription)
    }
}

