package com.example.gameshub.data.remote.dto

import com.squareup.moshi.Json

data class IgdbGenreDto(
    @Json(name = "id")
    val id: Int = 0,
    @Json(name = "name")
    val name: String = ""
)

