package com.example.gameshub.data.remote.dto

import com.squareup.moshi.Json

data class IgdbGameDto(
    @Json(name = "id")
    val id: Int = 0,
    @Json(name = "name")
    val name: String = "",
    @Json(name = "rating")
    val rating: Double? = null,
    @Json(name = "first_release_date")
    val firstReleaseDateEpochSeconds: Long? = null,
    @Json(name = "summary")
    val summary: String? = null,
    @Json(name = "cover")
    val cover: IgdbCoverDto? = null,
    @Json(name = "screenshots")
    val screenshots: List<IgdbImageDto>? = null,
    @Json(name = "videos")
    val videos: List<IgdbVideoDto>? = null
)

data class IgdbCoverDto(
    @Json(name = "url")
    val url: String? = null,
    @Json(name = "image_id")
    val imageId: String? = null
)

data class IgdbImageDto(
    @Json(name = "image_id")
    val imageId: String? = null
)

data class IgdbVideoDto(
    @Json(name = "video_id")
    val videoId: String? = null
)

