package it.scoppelletti.spaceship.sample.model

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import it.scoppelletti.spaceship.ApplicationException
import it.scoppelletti.spaceship.sample.R
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class DefaultItemRepo @Inject constructor(): ItemRepo {
    private var _lastUpdate: Long = System.currentTimeMillis()

    override val lastUpdate: Long
        get() = _lastUpdate

    private val items: MutableList<Item> by lazy {
        MutableList(20, {
            Item(id = it + 1, code = "code${it + 1}",
                    desc = "Description ${it + 1}.")
        })
    }

    override fun createItem(item: Item): Single<Item> {
        return synchronized(items) {
            Observable.fromIterable(items)
                    .map {
                        if (it.code.orEmpty() == item.code.orEmpty()) {
                            throw ApplicationException(
                                    R.string.err_code_duplicate,
                                    arrayOf(it.code.orEmpty()),
                                    R.string.it_scoppelletti_cmd_save)
                        }

                        it.id
                    }
                    .reduce(0) { t1, t2 -> if (t2 > t1) t2 else t1 }
                    .map { maxId ->
                        val newItem = Item(maxId + 1, item.code, item.desc)
                        items.add(newItem)

                        setLastUpdate()
                        newItem
                    }
                    .delay(4, TimeUnit.SECONDS)
        }
    }

    override fun readItem(id: Int): Single<Item> {
        return synchronized(items) {
            Observable.fromIterable(items)
                    .filter { it.id == id }
                    .firstOrError()
                    .delay(4, TimeUnit.SECONDS)
        }
    }

    override fun updateItem(item: Item): Single<Item> {
        return synchronized(items) {
            Observable.fromIterable(items)
                    .takeUntil { it.id == item.id }
                    .count()
                    .map { pos ->
                        val idx: Int
                        val oldItem: Item
                        val newItem: Item

                        idx = (pos - 1).toInt()
                        if (idx < 0) {
                            throw ApplicationException(R.string.err_item_notfound,
                                    arrayOf(item.id),
                                    R.string.it_scoppelletti_cmd_update)
                        }

                        oldItem = items[idx]

                        // Properties id and code are not updatable
                        newItem = Item(oldItem.id, oldItem.code, item.desc)

                        items[idx] = newItem
                        setLastUpdate()
                        newItem
                    }
                    .delay(4, TimeUnit.SECONDS)
        }
    }

    override fun deleteItem(id: Int): Completable {
        return synchronized(items) {
            Observable.fromIterable(items)
                    .takeUntil { it.id == id }
                    .count()
                    .flatMapCompletable { pos ->
                        if (pos > 0L) {
                            items.removeAt((pos - 1).toInt())
                            setLastUpdate()
                        }

                        Completable.complete()
                    }
                    .delay(4, TimeUnit.SECONDS)
        }
    }

    override fun listItems(): Single<List<Item>> {
        return synchronized(items) {
            Single.just(items.toList())
                    .delay(4, TimeUnit.SECONDS)
        }
    }

    private fun setLastUpdate() {
        _lastUpdate = System.currentTimeMillis()
    }
}
