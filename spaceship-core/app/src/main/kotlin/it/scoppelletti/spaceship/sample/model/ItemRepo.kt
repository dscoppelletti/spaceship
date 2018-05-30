package it.scoppelletti.spaceship.sample.model

import io.reactivex.Completable
import io.reactivex.Single

interface ItemRepo {
    val lastUpdate: Long
    fun createItem(item: Item): Single<Item>
    fun readItem(id: Int) : Single<Item>
    fun updateItem(item: Item): Single<Item>
    fun deleteItem(id: Int): Completable
    fun listItems() : Single<List<Item>>
}
