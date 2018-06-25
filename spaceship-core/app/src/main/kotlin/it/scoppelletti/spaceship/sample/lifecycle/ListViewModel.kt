package it.scoppelletti.spaceship.sample.lifecycle

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import it.scoppelletti.spaceship.sample.model.ItemRepo
import javax.inject.Inject

class ListViewModel @Inject constructor(
        private val repo: ItemRepo
) : ViewModel() {
    private val _state: MutableLiveData<ListState> = MutableLiveData()
    private val disposables: CompositeDisposable = CompositeDisposable()

    init {
        _state.value = ListState.empty()
    }

    val state : LiveData<ListState>
        get() = _state

    fun list() {
        val ts: Long
        val lastLoad: Long
        val subscription: Disposable

        lastLoad = _state.value?.lastLoad ?: 0
        if (lastLoad >= repo.lastUpdate) {
            return
        }

        _state.value = _state.value?.withWaiting()
        ts = System.currentTimeMillis()
        subscription = repo.listItems()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ items ->
                    _state.value = ListState.create(items, ts)
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

