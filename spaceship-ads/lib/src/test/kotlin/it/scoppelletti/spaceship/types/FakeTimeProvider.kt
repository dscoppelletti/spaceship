package it.scoppelletti.spaceship.types

import java.util.Calendar

class FakeTimeProvider : TimeProvider {

    private var _currentTime: Calendar = Calendar.getInstance()

    override fun currentTime(): Calendar = _currentTime.clone() as Calendar

    fun setCurrentTime(value: Calendar) {
        _currentTime = value.clone() as Calendar
    }
}