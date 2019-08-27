package it.scoppelletti.spaceship.sample.lifecycle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.scoppelletti.spaceship.StdlibExt
import it.scoppelletti.spaceship.sample.model.Item
import it.scoppelletti.spaceship.sample.model.ItemRepo
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class ListViewModel @Inject constructor(

        @Named(StdlibExt.DEP_MAINDISPATCHER)
        dispatcher: CoroutineDispatcher,

        private val repo: ItemRepo
) : ViewModel() {

    private val scope = CoroutineScope(dispatcher + Job())
    private val _state = MutableLiveData<ListState>()

    init {
        _state.value = ListState.empty()
    }

    val state : LiveData<ListState> = _state

    fun list() = scope.launch {
        val ts: Long
        val lastLoad: Long
        val items: List<Item>

        try {
            lastLoad = _state.value?.lastLoad ?: 0
            if (lastLoad >= repo.lastUpdate) {
                return@launch
            }

            _state.value = _state.value?.withWaiting()
            ts = System.currentTimeMillis()
            items = repo.listItems()
            _state.value = ListState.create(items, ts)
        } catch (ex: CancellationException) {
            throw ex
        } catch (ex: Exception) {
            _state.value = _state.value?.withError(ex)
        }
    }

    override fun onCleared() {
        scope.cancel()
        super.onCleared()
    }
}

