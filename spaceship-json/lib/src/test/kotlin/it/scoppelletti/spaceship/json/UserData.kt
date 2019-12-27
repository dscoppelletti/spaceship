package it.scoppelletti.spaceship.json

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserData(
        val firstName: String?,
        val lastName: String,

        @SerializeNulls
        val nickName: String?
)
