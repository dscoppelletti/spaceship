package it.scoppelletti.spaceship.http

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import it.scoppelletti.spaceship.types.StringExt
import it.scoppelletti.spaceship.types.trimRaw
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class HttpApplicationExceptionTest {
    private lateinit var moshi: Moshi
    private lateinit var adapter: JsonAdapter<HttpApplicationException.Builder>

    @BeforeTest
    fun before() {
        moshi = Moshi.Builder().build()
        adapter = moshi.adapter(HttpApplicationException.Builder::class.java)
    }

    @Test
    fun testAdapter() {
        assertEquals("it.scoppelletti.spaceship.http.HttpApplicationException_BuilderJsonAdapter",
                adapter.javaClass.name, "className")
        assertEquals("GeneratedJsonAdapter(HttpApplicationException.Builder)",
                adapter.toString(), "toString")
    }

    @Test
    fun testFull() {
        val builder: HttpApplicationException.Builder?
        val json: String = """{
            |"message" : "Error message",
            |"statusCode" : 500,
            |"error" : "Status description",
            |"exception" : "IOException",
            |"path" : "/api/resource",
            |"timestamp" : 1000
            |}
        """.trimRaw()

        builder = adapter.fromJson(json)
        assertNotNull(builder, "notNull")
        assertEquals("Error message",  builder?.message, "message")
        assertEquals(500, builder?.statusCode, "statusCode")
        assertEquals("Status description", builder?.error, "error")
        assertEquals("IOException", builder?.exception, "exception")
        assertEquals("/api/resource", builder?.path, "path")
        assertEquals(1000L, builder?.timestamp, "timestamp")
    }

    @Test
    fun testDefault() {
        val builder: HttpApplicationException.Builder?
        val json: String = """{
            |"dummy" : 0
            |}
        """.trimRaw()

        builder = adapter.fromJson(json)
        assertNotNull(builder, "notNull")
        assertEquals(StringExt.EMPTY,  builder?.message, "message")
        assertEquals(0, builder?.statusCode, "statusCode")
        assertNull(builder?.error, "error")
        assertEquals(StringExt.EMPTY, builder?.exception, "exception")
        assertNull(builder?.path, "path")
        assertEquals(0L, builder?.timestamp, "timestamp")
    }

    @Test
    fun testNull() {
        val json: String = "null"

        assertFailsWith(JsonDataException::class) {
            // Expected BEGIN_OBJECT but was NULL at path $
            adapter.fromJson(json)
        }
    }

    @Test
    fun testNonNull() {
        val json: String = "null"

        assertFailsWith(JsonDataException::class) {
            // Unexpected null at $
            adapter.nonNull().fromJson(json)
        }
    }

    @Test
    fun testNullSafe() {
        val builder: HttpApplicationException.Builder?
        val json: String = "null"

        builder = adapter.nullSafe().fromJson(json)
        assertNull(builder, "null")
    }
}