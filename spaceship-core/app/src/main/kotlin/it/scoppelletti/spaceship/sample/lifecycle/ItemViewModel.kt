package it.scoppelletti.spaceship.sample.lifecycle

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import it.scoppelletti.spaceship.sample.R
import it.scoppelletti.spaceship.sample.model.Item
import it.scoppelletti.spaceship.sample.model.ItemRepo
import javax.inject.Inject

class ItemViewModel @Inject constructor(
        private val repo: ItemRepo
): ViewModel() {
    private val _state: MutableLiveData<ItemState> = MutableLiveData()
    private val _form: ItemForm = ItemForm()
    private val disposables: CompositeDisposable = CompositeDisposable()

    init {
        _state.value = ItemState.create(Item())
    }

    val state: LiveData<ItemState>
        get() = _state

    val form: ItemForm
        get() = _form

    fun create() {
        val newItem: Item
        val subscription: Disposable

        _state.value = _state.value?.withWaiting()
        newItem = Item(form.id, form.code, form.desc)

        subscription = repo.createItem(newItem)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ item ->
                    _state.value = ItemState.create(item, R.string.msg_created)
                }, { ex ->
                    _state.value = _state.value?.withError(ex)
                })
        disposables.add(subscription)
    }

    fun read(itemId: Int) {
        val subscription: Disposable

        _state.value?.item?.peek()?.let { item ->
            if (item.id == itemId) {
                return
            }
        }

        _state.value = _state.value?.withWaiting()
        subscription = repo.readItem(itemId)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ item ->
                    _state.value = ItemState.create(item)
                }, { ex ->
                    _state.value = _state.value?.withError(ex)
                })
        disposables.add(subscription)
    }

    fun update() {
        val updatingItem: Item
        val updatedItem: Item
        val subscription: Disposable

        _state.value = _state.value?.withWaiting()
        updatingItem = _state.value?.item?.peek()!!
        updatedItem = Item(updatingItem.id, updatingItem.code, form.desc)

        subscription = repo.updateItem(updatedItem)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ item ->
                    _state.value = ItemState.create(item, R.string.msg_updated)
                }, { ex ->
                    _state.value = _state.value?.withError(ex)
                })
        disposables.add(subscription)
    }

    fun delete() {
        val deletingItem: Item
        val subscription: Disposable

        _state.value = _state.value?.withWaiting()
        deletingItem = _state.value?.item?.peek()!!

        subscription = repo.deleteItem(deletingItem.id)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _state.value = ItemState.create(
                            Item(0, form.code, form.desc), R.string.msg_deleted)
                }, { ex ->
                    _state.value = _state.value?.withError(ex)
                })
        disposables.add(subscription)
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}
