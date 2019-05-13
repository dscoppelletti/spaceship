package it.scoppelletti.spaceship.security.sample.lifecycle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.scoppelletti.spaceship.CoreExt
import it.scoppelletti.spaceship.security.CryptoProvider
import it.scoppelletti.spaceship.security.sample.R
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import kotlin.coroutines.CoroutineContext

class KeyViewModel @Inject constructor(

        @Named(CoreExt.DEP_MAINDISPATCHER)
        dispatcher: CoroutineDispatcher,

        private val cryptoProvider: CryptoProvider
): ViewModel(), CoroutineScope {

    private val _state = MutableLiveData<MainState>()
    private val _form = KeyForm()
    private val job = Job()

    init {
        _state.value = MainState.create()
    }

    override val coroutineContext: CoroutineContext = dispatcher + job

    val state: LiveData<MainState> = _state

    val form: KeyForm = _form

    fun createSecretKey(alias: String, expire: Int) = launch {
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
        job.cancel()
        super.onCleared()
    }
}

