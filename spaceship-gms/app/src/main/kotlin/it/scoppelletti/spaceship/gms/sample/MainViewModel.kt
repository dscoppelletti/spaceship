package it.scoppelletti.spaceship.gms.sample

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.scoppelletti.spaceship.StdlibExt
import it.scoppelletti.spaceship.gms.app.makeGooglePlayServicesAvailable
import it.scoppelletti.spaceship.lifecycle.SingleEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException
import javax.inject.Inject
import javax.inject.Named

class MainViewModel @Inject constructor(

    @Named(StdlibExt.DEP_MAINDISPATCHER)
    dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val scope = CoroutineScope(dispatcher + Job())
    private val _state = MutableLiveData<SingleEvent<GoogleApiState>>()

    val state: LiveData<SingleEvent<GoogleApiState>> = _state

    fun makeGmsAvailable(activity: Activity) = scope.launch {
        try {
            makeGooglePlayServicesAvailable(activity)
            _state.value = SingleEvent(GoogleApiState())
        } catch (ex: CancellationException) {
            throw ex
        } catch (ex: Exception) {
            _state.value = SingleEvent(GoogleApiState(ex))
        }
    }

    override fun onCleared() {
        scope.cancel()
        super.onCleared()
    }
}

data class GoogleApiState(
        val err: Exception? = null
)
