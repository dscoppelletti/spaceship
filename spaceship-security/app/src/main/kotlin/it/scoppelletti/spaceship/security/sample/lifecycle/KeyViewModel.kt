package it.scoppelletti.spaceship.security.sample.lifecycle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.scoppelletti.spaceship.StdlibExt
import it.scoppelletti.spaceship.security.CryptoProvider
import it.scoppelletti.spaceship.security.sample.R
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class KeyViewModel @Inject constructor(
        private val cryptoProvider: CryptoProvider,

        @Named(StdlibExt.DEP_MAINDISPATCHER)
        dispatcher: CoroutineDispatcher
): ViewModel() {

    private val scope = CoroutineScope(dispatcher + Job())
    private val _state = MutableLiveData<MainState>()
    private val _form = KeyForm()

    init {
        _state.value = MainState.create()
    }

    val state: LiveData<MainState> = _state

    val form: KeyForm = _form

    fun createSecretKey(alias: String, expire: Int) = scope.launch {
        try {
            _state.value = _state.value?.withWaiting()
            cryptoProvider.newSecretKey(alias, expire)
            _state.value = MainState.create(R.string.msg_keyGenerated)
        } catch (ex: CancellationException) {
            throw ex
        } catch (ex: Exception) {
            _state.value = _state.value?.withError(ex)
        }
    }

    fun setError(ex: Throwable) {
        _state.value = _state.value?.withError(ex)
    }

    override fun onCleared() {
        scope.cancel()
        super.onCleared()
    }
}

