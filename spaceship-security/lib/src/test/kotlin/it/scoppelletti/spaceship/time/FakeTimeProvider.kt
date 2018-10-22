package it.scoppelletti.spaceship.time

import it.scoppelletti.spaceship.types.TimeProvider
import java.util.Calendar

class FakeTimeProvider : TimeProvider {

    private var _currentTime: Calendar

    init {
        _currentTime = Calendar.getInstance()
    }

    override fun currentTime(): Calendar = _currentTime.clone() as Calendar

    fun setCurrentTime(value: Calendar) {
        _currentTime = value.clone() as Calendar
    }
}