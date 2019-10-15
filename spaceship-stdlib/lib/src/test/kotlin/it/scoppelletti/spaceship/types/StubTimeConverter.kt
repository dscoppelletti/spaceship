package it.scoppelletti.spaceship.types

import it.scoppelletti.spaceship.i18n.I18NProvider

class StubTimeConverter(
        secs: Boolean,
        private var _is24HourFormat: Boolean,
        i18nProvider: I18NProvider
) : AbstractTimeConverter(secs, i18nProvider) {

    override fun pattern(): String {
        throw NotImplementedError()
    }

    override fun is24HourFormat(): Boolean =_is24HourFormat
}