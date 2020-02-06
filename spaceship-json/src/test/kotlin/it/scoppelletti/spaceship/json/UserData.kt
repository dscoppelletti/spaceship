package it.scoppelletti.spaceship.json

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserData(
        val lastName: String,
        val firstName: String?
)
