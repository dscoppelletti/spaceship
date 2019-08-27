
@file:Suppress("JoinDeclarationAndAssignment")

package it.scoppelletti.spaceship.sample.model

import it.scoppelletti.spaceship.ApplicationException
import it.scoppelletti.spaceship.StdlibExt
import it.scoppelletti.spaceship.sample.i18n.SampleMessages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.threeten.bp.Clock
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class DefaultItemRepo @Inject constructor(

        @Named(StdlibExt.DEP_UTCCLOCK)
        private val utcClock: Clock
): ItemRepo {
    private var _lastUpdate: Long = utcClock.millis()

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
                    throw ApplicationException(SampleMessages.errorCodeDuplicate(
                            item.code.orEmpty()))
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
                        throw ApplicationException(
                                SampleMessages.errorItemNotFound(id))
            }

    override suspend fun updateItem(item: Item): Item =
            withContext(Dispatchers.Default) {
                val idx: Int
                val newItem: Item
                val oldItem: Item

                delay(4000)
                idx = items.indexOfFirst { it.id == item.id }
                if (idx < 0) {
                    throw ApplicationException(
                            SampleMessages.errorItemNotFound(item.id))
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
        _lastUpdate = utcClock.millis()
    }
}
