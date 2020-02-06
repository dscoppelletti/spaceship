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

class TimeConverter12SecsTest {

    private lateinit var timeConverter: TimeConverter

    @BeforeTest
    fun setUp() {
        val i18nProvider = StubI18NProvider(false)
        timeConverter = i18nProvider.timeConverter(true)
    }

    @Test
    fun format() {
        var time: LocalTime

        assertNull(timeConverter.format(null), "null")

        time = LocalTime.of(10, 17, 24)
        assertEquals("10:17:24 AM", timeConverter.format(time), "10:17:24")

        time = LocalTime.of(14, 5)
        assertEquals(" 2:05:00 PM", timeConverter.format(time), "14:05:00")
    }

    @Test
    fun parse() {
        var time: LocalTime?

        assertNull(timeConverter.parse(null), "null")
        assertNull(timeConverter.parse(""), "empty")
        assertNull(timeConverter.parse("   "), "blanks")

        time = timeConverter.parse("2:37:42am")
        assertEquals(LocalTime.of(2, 37, 42), time, "2:37:42am")

        time = timeConverter.parse("08:5 PM")
        assertEquals(LocalTime.of(20, 5, 0), time, "08:5 PM")

        time = timeConverter.parse("2.37,42Pm")
        assertEquals(LocalTime.of(14, 37, 42), time, "2.37,42Pm")

        time = timeConverter.parse("   2:37 AM  ")
        assertEquals(LocalTime.of(2, 37, 0), time, "(blanks)2:37 AM(blanks)")

        assertFailsWith(DateTimeParseException::class, "14:37:42") {
            timeConverter.parse("14:37:42")
        }

        assertFailsWith(DateTimeParseException::class, "14:37 PM") {
            timeConverter.parse("14:37 PM")
        }

        assertFailsWith(DateTimeParseException::class, "xxx2:37:42 PM") {
            timeConverter.parse("xxx2:37:42 PM")
        }

        assertFailsWith(DateTimeParseException::class, "2:37AMxxx") {
            timeConverter.parse("2:37AMxxx")
        }
    }
}