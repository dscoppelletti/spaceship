package it.scoppelletti.spaceship.io

import kotlinx.coroutines.runBlocking
import okio.Okio
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class IOExtTest {
    private lateinit var data: String

    @BeforeTest
    fun setUp() {
        val chars : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        data = (0 until DEFAULT_BUFFER_SIZE * 5)
                .map { chars.random() }
                .joinToString("")
    }

    @Test
    @Suppress("BlockingMethodInNonBlockingContext")
    fun copy() = runBlocking {
        val file1 = createTempFile()
        val sink1 = Okio.sink(file1)
        val bufSink = Okio.buffer(sink1)

        bufSink.writeUtf8(data)
        bufSink.close()

        val source1 = Okio.source(file1)
        val file2 = createTempFile()
        val sink2 = Okio.sink(file2)

        copy(source1, sink2)
        source1.close()
        sink2.close()

        val source2 = Okio.source(file2)
        val bufSource = Okio.buffer(source2)

        val testData = bufSource.readUtf8()
        bufSource.close()

        assertEquals(data, testData)
    }
}
