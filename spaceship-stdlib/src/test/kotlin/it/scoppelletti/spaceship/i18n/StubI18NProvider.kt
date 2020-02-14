package it.scoppelletti.spaceship.i18n

import it.scoppelletti.spaceship.types.DateConverter
import it.scoppelletti.spaceship.types.StubTimeConverter
import it.scoppelletti.spaceship.types.StubDateConverter
import it.scoppelletti.spaceship.types.TimeConverter
import org.threeten.bp.ZoneId
import java.util.Locale

class StubI18NProvider(
        private val is24HourFormat: Boolean
) : I18NProvider {

    override fun currentLocale(): Locale = Locale.ITALY

    override fun currentZoneId(): ZoneId = ZoneId.of("Europe/Rome")

    override fun dateConverter(): DateConverter = StubDateConverter(this)

    override fun timeConverter(secs: Boolean): TimeConverter =
            StubTimeConverter(secs, is24HourFormat, this)
}
