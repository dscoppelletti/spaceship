package it.scoppelletti.spaceship.sample.lifecycle

import it.scoppelletti.spaceship.lifecycle.SingleEvent
import it.scoppelletti.spaceship.sample.model.Item

data class ListState(
        val items: List<Item>,
        val lastLoad: Long,
        val waiting: Boolean,
        val error: SingleEvent<Throwable>?
) {
    fun withWaiting(): ListState = copy(waiting = true, error = null)
    fun withError(ex: Throwable) =
            copy(error = SingleEvent(ex), waiting = false)

    companion object {
        fun create(items: List<Item>, timestamp: Long): ListState =
                ListState(items, timestamp, false, null)

        fun empty(): ListState = ListState(emptyList(), 0, false, null)
    }
}
