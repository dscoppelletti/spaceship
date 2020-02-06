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

class TimeConverter12NoSecsTest {

    private lateinit var timeConverter: TimeConverter

    @BeforeTest
    fun setUp() {
        val i18nProvider = StubI18NProvider(false)
        timeConverter = i18nProvider.timeConverter(false)
    }

    @Test
    fun format() {
        var time: LocalTime

        assertNull(timeConverter.format(null), "null")

        time = LocalTime.of(10, 17, 24)
        assertEquals("10:17 AM", timeConverter.format(time), "10:17:24")

        time = LocalTime.of(14, 5, 15)
        assertEquals(" 2:05 PM", timeConverter.format(time), "14:05:15")

        time = LocalTime.of(20, 0, 40)
        assertEquals(" 8:00 PM", timeConverter.format(time), "20:00:40")
    }

    @Test
    fun parse() {
        var time: LocalTime?

        assertNull(timeConverter.parse(null), "null")
        assertNull(timeConverter.parse(""), "empty")
        assertNull(timeConverter.parse("   "), "blanks")

        time = timeConverter.parse("10:37am")
        assertEquals(LocalTime.of(10, 37, 0), time, "10:37am")

        time = timeConverter.parse("08:5 PM")
        assertEquals(LocalTime.of(20, 5, 0), time, "08:5 PM")

        time = timeConverter.parse("2.37Pm")
        assertEquals(LocalTime.of(14, 37, 0), time, "2.37Pm")

        time = timeConverter.parse("   2:37 AM  ")
        assertEquals(LocalTime.of(2, 37, 0), time, "(blanks)2:37 AM(blanks)")

        assertFailsWith(DateTimeParseException::class, "2:37:42 AM") {
            timeConverter.parse("2:37:42 AM")
        }

        assertFailsWith(DateTimeParseException::class, "14:37 PM") {
            timeConverter.parse("14:37 PM")
        }

        assertFailsWith(DateTimeParseException::class, "xxx2:37PM") {
            timeConverter.parse("xxx2:37PM")
        }

        assertFailsWith(DateTimeParseException::class, "2:37AMxxx") {
            timeConverter.parse("2:37AMxxx")
        }
    }
}