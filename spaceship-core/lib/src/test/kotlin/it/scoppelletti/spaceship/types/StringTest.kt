package it.scoppelletti.spaceship.types

import kotlin.test.Test
import kotlin.test.assertEquals

class StringTest {

    @Test
    fun testTrimRaw() {
        assertEquals("foo bar", """foo
            |bar""".trimMargin().trimRaw())
    }
}