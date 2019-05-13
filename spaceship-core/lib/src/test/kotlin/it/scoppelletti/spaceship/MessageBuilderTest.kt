package it.scoppelletti.spaceship

import android.content.res.Resources
import it.scoppelletti.spaceship.types.trimRaw
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)
class MessageBuilderTest {

    @Mock
    private lateinit var resources: Resources

    @BeforeTest
    fun setUp() {
        Mockito.`when`(resources.getString(R.string.message_builder))
                .thenReturn("Test")
        Mockito.`when`(resources.getString(R.string.message_builder_args,
                "foo", 5, null, "bar"))
                .thenReturn(String.format("Test %s %d %s %s", "foo", 5, null,
                        "bar"))
    }

    @Test
    fun testSimple() {
        val s: String

        val builder = MessageBuilder.make(R.string.message_builder) { }
        assertEquals("""MessageBuilder(messageId=${R.string.message_builder},
            |args=null)""".trimMargin().trimRaw(),
                builder.toString(),
                "Message without arguments.")

        assertTrue(builder.isSimple, "Simple message.")

        s = builder.build(resources)
        assertEquals("Test", s, "Build simple message.")
    }

    @Test
    fun testArgs() {
        val s: String

        val builder = MessageBuilder.make(R.string.message_builder_args) {
            arguments {
                add("foo")
                add(5)
                add(null)
                add("bar")
            }
        }

        assertFalse(builder.isSimple, "Not simple message.")

        assertEquals("""
            |MessageBuilder(messageId=${R.string.message_builder_args},
            |args=foo, 5, null, bar)""".trimMargin().trimRaw(),
                builder.toString(),
                "Message with arguments.")

        s = builder.build(resources)
        assertEquals("Test foo 5 null bar", s, "Build message with arguments.")
    }
}