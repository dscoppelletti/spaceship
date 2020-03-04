@file:Suppress("RemoveRedundantQualifierName")

package it.scoppelletti.spaceship.security.sample.lifecycle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.scoppelletti.spaceship.lifecycle.ViewModelProviderEx
import it.scoppelletti.spaceship.security.CryptoProvider
import it.scoppelletti.spaceship.security.sample.R
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import javax.inject.Inject

class KeyViewModel(
        private val cryptoProvider: CryptoProvider
): ViewModel() {

    private val _state = MutableLiveData<MainState>()
    private val _form = KeyForm()

    init {
        _state.value = MainState.create()
    }

    val state: LiveData<MainState> = _state

    val form: KeyForm = _form

    fun createSecretKey(alias: String, expire: Int) = viewModelScope.launch {
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
}

class KeyViewModelFactory @Inject constructor(
        private val cryptoProvider: CryptoProvider
) : ViewModelProviderEx.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(handle: SavedStateHandle): T =
            KeyViewModelFactory(cryptoProvider) as T
}
