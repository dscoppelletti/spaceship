package it.scoppelletti.spaceship.types

import it.scoppelletti.spaceship.i18n.I18NProvider
import org.threeten.bp.temporal.ChronoField

class StubDateConverter(
        i18nProvider: I18NProvider
) : AbstractDateConverter(i18nProvider) {

    override fun pattern(): String {
        throw NotImplementedError()
    }

    override fun getDateFormatOrder(): Array<ChronoField> =
            arrayOf(ChronoField.DAY_OF_MONTH, ChronoField.MONTH_OF_YEAR,
                    ChronoField.YEAR)
}