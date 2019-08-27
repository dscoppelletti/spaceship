package it.scoppelletti.spaceship.sample.lifecycle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.scoppelletti.spaceship.StdlibExt
import it.scoppelletti.spaceship.sample.R
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

class ItemViewModel @Inject constructor(

        @Named(StdlibExt.DEP_MAINDISPATCHER)
        dispatcher: CoroutineDispatcher,

        private val repo: ItemRepo
): ViewModel() {

    private val scope = CoroutineScope(dispatcher + Job())
    private val _state = MutableLiveData<ItemState>()
    private val _form = ItemForm()

    init {
        _state.value = ItemState.create(Item())
    }

    val state: LiveData<ItemState> = _state
    val form: ItemForm = _form

    fun create() = scope.launch {
        var item: Item

        try {
            _state.value = _state.value?.withWaiting()
            item = Item(form.id, form.code, form.desc)
            item = repo.createItem(item)

            _state.value = ItemState.create(item, R.string.msg_created)
        } catch (ex: CancellationException) {
            throw ex
        } catch (ex: Exception) {
            _state.value = _state.value?.withError(ex)
        }
    }

    fun read(itemId: Int) = scope.launch {
        val item: Item

        try {
            _state.value?.item?.peek()?.let {
                if (it.id == itemId) {
                    return@launch
                }
            }

            _state.value = _state.value?.withWaiting()
            item = repo.readItem(itemId)
            _state.value = ItemState.create(item)
        } catch (ex: CancellationException) {
            throw ex
        } catch (ex: Exception) {
            _state.value = _state.value?.withError(ex)
        }
    }

    fun update() = scope.launch {
        var item: Item

        try {
            _state.value = _state.value?.withWaiting()
            item = _state.value?.item?.peek()!!
            item = Item(item.id, item.code, form.desc)
            item = repo.updateItem(item)

            _state.value = ItemState.create(item, R.string.msg_updated)
        } catch (ex: CancellationException) {
            throw ex
        } catch (ex: Exception) {
            _state.value = _state.value?.withError(ex)
        }
    }

    fun delete() = scope.launch {
        val item: Item

        try {
            _state.value = _state.value?.withWaiting()
            item = _state.value?.item?.peek()!!
            repo.deleteItem(item.id)

            _state.value = ItemState.create(Item(0, form.code, form.desc),
                    R.string.msg_deleted)
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

