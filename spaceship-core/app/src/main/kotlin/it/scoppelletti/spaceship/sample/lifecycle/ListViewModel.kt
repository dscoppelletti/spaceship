package it.scoppelletti.spaceship.sample.lifecycle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.scoppelletti.spaceship.CoreExt
import it.scoppelletti.spaceship.sample.model.Item
import it.scoppelletti.spaceship.sample.model.ItemRepo
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import kotlin.coroutines.CoroutineContext

class ListViewModel @Inject constructor(

        @Named(CoreExt.DEP_MAINDISPATCHER)
        dispatcher: CoroutineDispatcher,

        private val repo: ItemRepo
) : ViewModel(), CoroutineScope {
    private val _state = MutableLiveData<ListState>()
    private val job: Job = Job()

    init {
        _state.value = ListState.empty()
    }

    override val coroutineContext: CoroutineContext = dispatcher + job

    val state : LiveData<ListState> = _state

    fun list() = launch {
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
        job.cancel()
        super.onCleared()
    }
}

