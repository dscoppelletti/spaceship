package it.scoppelletti.spaceship.types

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StringTest {

    @Test
    fun empty() {
        assertTrue(StringExt.EMPTY.isEmpty())
    }

    @Test
    fun joinLines() {
        assertEquals("foo bar", """foo
            |bar""".trimMargin().joinLines())
    }
}