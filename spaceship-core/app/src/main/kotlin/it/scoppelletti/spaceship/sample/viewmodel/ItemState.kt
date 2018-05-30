package it.scoppelletti.spaceship.sample.viewmodel

import it.scoppelletti.spaceship.SingleEvent
import it.scoppelletti.spaceship.sample.model.Item

data class ItemState(
        val item: SingleEvent<Item>,
        val waiting: Boolean,
        val messageId: SingleEvent<Int>?,
        val error: SingleEvent<Throwable>?
) {
    fun withWaiting(): ItemState =
            copy(waiting = true, messageId = null, error = null)
    fun withError(ex: Throwable) =
            copy(error = SingleEvent(ex), waiting = false, messageId = null)

    companion object {
        fun create(item: Item, messageId: Int = 0): ItemState =
                ItemState(item = SingleEvent(item),
                        waiting = false,
                        messageId = if (messageId > 0) SingleEvent(messageId)
                            else null,
                        error = null)
    }
}