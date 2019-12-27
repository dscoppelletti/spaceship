package it.scoppelletti.spaceship.json

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SerializeNullsTest {

    private lateinit var adapter: JsonAdapter<UserData>

    @BeforeTest
    fun setUp() {
        val moshi = Moshi.Builder()
                .add(SerializeNulls.AdapterFactory)
                .build()
        adapter = moshi.adapter(UserData::class.java)
    }

    @Test
    fun serializeNulls() {
        val user = UserData(
                firstName = null,
                lastName = "Doe",
                nickName = null
        )

        val json = adapter.toJson(user)
        assertEquals("""{"lastName":"Doe","nickName":null}""", json)
    }
}

