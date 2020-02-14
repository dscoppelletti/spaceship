package it.scoppelletti.spaceship.security.sample.lifecycle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    private val _state = MutableLiveData<MainState>()

    val state: LiveData<MainState> = _state

    fun setState(state: MainState) {
        _state.value = state
    }
}

