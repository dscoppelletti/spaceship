package it.scoppelletti.spaceship.security.sample.lifecycle

import it.scoppelletti.spaceship.lifecycle.SingleEvent

data class MainState(
        val waiting: Boolean,
        val messageId: SingleEvent<Int>?,
        val error: SingleEvent<Throwable>?
) {
    fun withWaiting(): MainState =
            copy(waiting = true, messageId = null, error = null)
    fun withError(ex: Throwable) =
            copy(error = SingleEvent(ex), waiting = false, messageId = null)

    companion object {
        fun create(messageId: Int = 0): MainState =
                MainState(waiting = false,
                        messageId = if (messageId > 0) SingleEvent(messageId)
                        else null,
                        error = null)
    }
}
