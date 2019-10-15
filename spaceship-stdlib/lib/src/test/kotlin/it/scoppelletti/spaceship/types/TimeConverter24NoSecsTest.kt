@file:Suppress("JoinDeclarationAndAssignment")

package it.scoppelletti.spaceship.types

import it.scoppelletti.spaceship.i18n.StubI18NProvider
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeParseException
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class TimeConverter24NoSecsTest {

    private lateinit var timeConverter: TimeConverter

    @BeforeTest
    fun setUp() {
        val i18nProvider = StubI18NProvider(true)
        timeConverter = i18nProvider.timeConverter(false)
    }

    @Test
    fun format() {
        var time: LocalTime

        assertNull(timeConverter.format(null), "null")

        time = LocalTime.of(5, 17, 24)
        assertEquals(" 5:17", timeConverter.format(time), "05:17:24")

        time = LocalTime.of(14, 5, 15)
        assertEquals("14:05", timeConverter.format(time), "14:05:15")

        time = LocalTime.of(20, 0, 40)
        assertEquals("20:00", timeConverter.format(time), "20:00:40")
    }

    @Test
    fun parse() {
        var time: LocalTime?

        assertNull(timeConverter.parse(null), "null")
        assertNull(timeConverter.parse(""), "empty")
        assertNull(timeConverter.parse("   "), "blanks")

        time = timeConverter.parse("08:5")
        assertEquals(LocalTime.of(8, 5, 0), time, "08:5")

        time = timeConverter.parse("14,37")
        assertEquals(LocalTime.of(14, 37, 0), time, "14,37")

        time = timeConverter.parse("   14:37  ")
        assertEquals(LocalTime.of(14, 37, 0), time, "(blanks)14:37(blanks)")

        assertFailsWith(DateTimeParseException::class, "14:37:42") {
            timeConverter.parse("14:37:42")
        }

        assertFailsWith(DateTimeParseException::class, "xxx14:37") {
            timeConverter.parse("xxx14:37")
        }

        assertFailsWith(DateTimeParseException::class, "14:37xxx") {
            timeConverter.parse("14:37xxx")
        }
    }
}