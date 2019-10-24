package it.scoppelletti.spaceship.types

import org.threeten.bp.Clock
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId

class FakeClock(var impl: Clock) : Clock() {

    override fun instant(): Instant = impl.instant()

    override fun withZone(zone: ZoneId?): Clock = impl.withZone(zone)

    override fun getZone(): ZoneId = impl.zone
}
