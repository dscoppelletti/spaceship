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

class TimeConverter24SecsTest {

    private lateinit var timeConverter: TimeConverter

    @BeforeTest
    fun setUp() {
        val i18nProvider = StubI18NProvider(true)
        timeConverter = i18nProvider.timeConverter(true)
    }

    @Test
    fun format() {
        var time: LocalTime

        assertNull(timeConverter.format(null), "null")

        time = LocalTime.of(5, 17, 24)
        assertEquals(" 5:17:24", timeConverter.format(time), "05:17:24")

        time = LocalTime.of(14, 5)
        assertEquals("14:05:00", timeConverter.format(time), "14:05:00")
    }

    @Test
    fun parse() {
        var time: LocalTime?

        assertNull(timeConverter.parse(null), "null")
        assertNull(timeConverter.parse(""), "empty")
        assertNull(timeConverter.parse("   "), "blanks")

        time = timeConverter.parse("14:37:42")
        assertEquals(LocalTime.of(14, 37, 42), time, "14:37:42")

        time = timeConverter.parse("08:5")
        assertEquals(LocalTime.of(8, 5, 0), time, "08:5")

        time = timeConverter.parse("14.37,42")
        assertEquals(LocalTime.of(14, 37, 42), time, "14.37,42")

        time = timeConverter.parse("   14:37:42  ")
        assertEquals(LocalTime.of(14, 37, 42), time, "(blanks)14:37:42(blanks)")

        assertFailsWith(DateTimeParseException::class, "xxx14:37:42") {
            timeConverter.parse("xxx14:37:42")
        }

        assertFailsWith(DateTimeParseException::class, "14:37xxx") {
            timeConverter.parse("14:37xxx")
        }
    }
}