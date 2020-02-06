@file:Suppress("JoinDeclarationAndAssignment")

package it.scoppelletti.spaceship.types

import it.scoppelletti.spaceship.i18n.StubI18NProvider
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeParseException
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class DateConverterTest {

    private lateinit var dateConverter: DateConverter

    @BeforeTest
    fun setUp() {
        val i18nProvider = StubI18NProvider(false)
        dateConverter = i18nProvider.dateConverter()
    }

    @Test
    fun format() {
        assertNull(dateConverter.format(null), "null")

        val date = LocalDate.of(1972, 2, 26)
        assertEquals("26/02/1972", dateConverter.format(date), "26/02/1972")
    }

    @Test
    fun parse() {
        var date: LocalDate?

        assertNull(dateConverter.parse(null), "null")
        assertNull(dateConverter.parse(""), "empty")
        assertNull(dateConverter.parse("   "), "blanks")

        date = dateConverter.parse("26/02/1972")
        assertEquals(LocalDate.of(1972, 2, 26), date, "26/02/1972")

        assertFailsWith(DateTimeParseException::class, "26/02/72") {
            dateConverter.parse("26/02/72")
        }

        date = dateConverter.parse("26/2/1972")
        assertEquals(LocalDate.of(1972, 2, 26), date, "26/2/1972")

        assertFailsWith(DateTimeParseException::class, "2/26/72") {
            dateConverter.parse("2/26/1972")
        }

        date = dateConverter.parse("26-2-1972")
        assertEquals(LocalDate.of(1972, 2, 26), date, "26-2-1972")

        date = dateConverter.parse("   26.02.1972   ")
        assertEquals(LocalDate.of(1972, 2, 26), date,
                "(blanks)26.02.1972(blanks)")

        assertFailsWith(DateTimeParseException::class, "xxx26/02/1972") {
            dateConverter.parse("xxx26/02/1972")
        }

        assertFailsWith(DateTimeParseException::class, "26/02/1972xxx") {
            dateConverter.parse("26/02/1972xxx")
        }
    }
}
