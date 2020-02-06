package it.scoppelletti.spaceship.json

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

private const val SCALE = 2
private val ROUNDING_MODE = RoundingMode.HALF_EVEN

class DecimalAdapterTest {
    private lateinit var adapter: JsonAdapter<BigDecimal>

    @BeforeTest
    fun setUp() {
        val moshi = Moshi.Builder()
                .add(BigDecimal::class.java, DecimalAdapter(SCALE,
                        ROUNDING_MODE))
                .build()
        adapter = moshi.adapter(BigDecimal::class.java)
    }

    @Test
    fun formatValue() {
        assertEquals("\"2005.02\"", adapter.toJson(toBigDecimal("2005.02")),
                "2005.02")
        assertEquals("\"2005.02\"", adapter.toJson(toBigDecimal("2005.023")),
                "2005.023")
        assertEquals("\"2005.00\"", adapter.toJson(toBigDecimal("2005")),
                "2005")
    }

    @Test
    fun formatNull() {
        assertEquals("null", adapter.toJson(null))
    }

    @Test
    fun parseValue() {
        assertEquals(toBigDecimal("2005.02"), adapter.fromJson("\"2005.02\""),
                "2005.02")
        assertEquals(toBigDecimal("2005.02"), adapter.fromJson("\"2005.023\""),
                "2005.023")
        assertEquals(toBigDecimal("2005.00"), adapter.fromJson("\"2005\""),
                "2005")
    }

    @Test
    fun parseNull() {
        assertEquals(null, adapter.fromJson("null"), "null")
        assertEquals(null, adapter.fromJson("\"\""), "(empty)")
        assertEquals(null, adapter.fromJson("\"   \""), "(blank)")
    }

    private fun toBigDecimal(x: String) =
            BigDecimal(x).setScale(SCALE, ROUNDING_MODE)
}


