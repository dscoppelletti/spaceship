
@file:Suppress("JoinDeclarationAndAssignment")

package it.scoppelletti.spaceship.sample.model

import it.scoppelletti.spaceship.applicationException
import it.scoppelletti.spaceship.sample.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultItemRepo @Inject constructor(): ItemRepo {
    private var _lastUpdate: Long = System.currentTimeMillis()

    override val lastUpdate: Long = _lastUpdate

    private val items: MutableList<Item> by lazy {
        MutableList(20) {
            Item(id = it + 1, code = "code${it + 1}",
                    desc = "Description ${it + 1}.")
        }
    }

    override suspend fun createItem(item: Item): Item =
            withContext(Dispatchers.Default) {
                val maxId: Int
                val newItem: Item
                val oldItem: Item?

                delay(4000)
                oldItem = items.find {
                    it.code.orEmpty() == item.code.orEmpty()
                }
                if (oldItem != null) {
                    throw applicationException {
                        message(R.string.err_code_duplicate) {
                            arguments {
                                add(item.code.orEmpty())
                            }
                        }
                    }
                }

                maxId = items.maxBy { it.id }?.id ?: 0

                newItem = Item(maxId + 1, item.code, item.desc)
                items.add(newItem)

                setLastUpdate()
                newItem
            }

    override suspend fun readItem(id: Int): Item =
            withContext(Dispatchers.Default) {
                delay(4000)
                items.find { it.id == id } ?:
                    throw applicationException {
                        message(R.string.err_item_notfound) {
                            arguments {
                                add(id)
                            }
                        }
                    }
            }

    override suspend fun updateItem(item: Item): Item =
            withContext(Dispatchers.Default) {
                val idx: Int
                val newItem: Item
                val oldItem: Item

                delay(4000)
                idx = items.indexOfFirst { it.id == item.id }
                if (idx < 0) {
                    throw applicationException {
                        message(R.string.err_item_notfound) {
                            arguments {
                                add(item.id)
                            }
                        }
                    }
                }

                oldItem = items[idx]

                // Properties id and code are not updatable
                newItem = Item(oldItem.id, oldItem.code, item.desc)

                items[idx] = newItem
                setLastUpdate()

                newItem
            }

    override suspend fun deleteItem(id: Int) =
            withContext(Dispatchers.Default) {
                val idx: Int

                delay(4000)
                idx = items.indexOfFirst { it.id == id }
                if (idx < 0) {
                    return@withContext
                }

                items.removeAt(idx)
                setLastUpdate()
            }

    override suspend fun listItems(): List<Item> =
            withContext(Dispatchers.Default) {
                delay(4000)
                items.toList()
            }

    private fun setLastUpdate() {
        _lastUpdate = System.currentTimeMillis()
    }
}
