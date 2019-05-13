package it.scoppelletti.spaceship.sample.model

interface ItemRepo {
    val lastUpdate: Long
    suspend fun createItem(item: Item): Item
    suspend fun readItem(id: Int) : Item
    suspend fun updateItem(item: Item): Item
    suspend fun deleteItem(id: Int)
    suspend fun listItems() : List<Item>
}
