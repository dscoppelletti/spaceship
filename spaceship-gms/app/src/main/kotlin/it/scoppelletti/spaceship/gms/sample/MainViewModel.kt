package it.scoppelletti.spaceship.gms.sample

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.scoppelletti.spaceship.gms.app.makeGooglePlayServicesAvailable
import it.scoppelletti.spaceship.lifecycle.SingleEvent
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException

class MainViewModel : ViewModel() {

    private val _state = MutableLiveData<SingleEvent<GoogleApiState>>()

    val state: LiveData<SingleEvent<GoogleApiState>> = _state

    fun makeGmsAvailable(activity: Activity) = viewModelScope.launch {
        try {
            makeGooglePlayServicesAvailable(activity)
            _state.value = SingleEvent(GoogleApiState())
        } catch (ex: CancellationException) {
            throw ex
        } catch (ex: Exception) {
            _state.value = SingleEvent(GoogleApiState(ex))
        }
    }
}

data class GoogleApiState(
        val err: Exception? = null
)
