package it.scoppelletti.spaceship.security.sample.lifecycle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class MainViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableLiveData<MainState>()

    val state: LiveData<MainState> = _state

    fun setState(state: MainState) {
        _state.value = state
    }
}

