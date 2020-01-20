package it.scoppelletti.spaceship.json

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class JsonAdapterTest {
    private lateinit var adapter: JsonAdapter<UserData>

    @BeforeTest
    fun setUp() {
        val moshi = Moshi.Builder().build()
        adapter = moshi.adapter(UserData::class.java)
    }

    @Test
    fun nonSerializeNull() {
        val data = UserData(
                lastName = "Doe",
                firstName = null
        )

        assertEquals("{\"lastName\":\"Doe\"}", adapter.toJson(data))
    }

    @Test
    fun serializeNull() {
        val data = UserData(
                lastName = "Doe",
                firstName = null
        )

        assertEquals("{\"lastName\":\"Doe\",\"firstName\":null}",
                adapter.serializeNulls().toJson(data))
    }

    @Test
    fun deserializeMissing() {
        val json = "{\"lastName\":\"Doe\"}"
        val data: UserData?

        data = adapter.fromJson(json)
        assertEquals("Doe", data?.lastName, "lastName=Doe")
        assertNull(data?.firstName, "firstName=null")
    }

    @Test
    fun deserializeNull() {
        val json = "{\"lastName\":\"Doe\",\"firstName\":null}"
        val data: UserData?

        data = adapter.fromJson(json)
        assertEquals("Doe", data?.lastName, "lastName=Doe")
        assertNull(data?.firstName, "firstName=null")
    }

    @Test
    fun failNull() {
        val json = "{\"lastName\":null,\"firstName\":null}"

        assertFailsWith(JsonDataException::class) {
            // Non-null value 'lastName' was null at $.lastName
            adapter.fromJson(json)
        }
    }

    @Test
    fun defaultNull() {
        val json = "null"
        val data: UserData?

        data = adapter.fromJson(json)
        assertNull(data)
    }

    @Test
    fun nonNull() {
        val json = "null"

        assertFailsWith(JsonDataException::class) {
            // Unexpected null at $
            adapter.nonNull().fromJson(json)
        }
    }

    @Test
    fun nullSafe() {
        val json = "null"
        val data: UserData?

        data = adapter.nullSafe().fromJson(json)
        assertNull(data)
    }
}
