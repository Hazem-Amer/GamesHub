package com.example.gameshub.data.remote.mappers

import com.example.gameshub.data.remote.dto.IgdbGameDto
import com.example.gameshub.data.remote.dto.IgdbGenreDto
import com.example.gameshub.domain.model.Game
import com.example.gameshub.domain.model.GameDetails
import com.example.gameshub.domain.model.Genre
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

private const val IGDB_IMAGE_BASE_URL: String = "https://images.igdb.com/igdb/image/upload"

private enum class IgdbImageSize(val value: String) {
    CoverBig("cover_big"),
    ScreenshotBig("screenshot_big"),
    ScreenshotMed("screenshot_med"),
    Hd1080p("1080p")
}

fun IgdbGameDto.toDomainGame(): Game {
    return Game(
        id = id,
        name = name,
        backgroundImageUrl = cover?.imageId?.toIgdbImageUrl(IgdbImageSize.CoverBig)
            ?: cover?.url?.toFullImageUrl(),
        rating = rating ?: 0.0
    )
}

fun IgdbGameDto.toDomainGameDetails(): GameDetails {
    val screenshotUrls = screenshots.orEmpty()
        .mapNotNull { it.imageId?.toIgdbImageUrl(IgdbImageSize.ScreenshotBig) }

    val trailerIds = videos.orEmpty()
        .mapNotNull { it.videoId?.trim()?.takeIf { id -> id.isNotBlank() } }

    return GameDetails(
        id = id,
        name = name,
        backgroundImageUrl = cover?.imageId?.toIgdbImageUrl(IgdbImageSize.Hd1080p)
            ?: cover?.url?.toFullImageUrl(),
        released = firstReleaseDateEpochSeconds?.toIsoDateString(),
        rating = rating ?: 0.0,
        description = summary.orEmpty(),
        screenshotImageUrls = screenshotUrls,
        trailerYoutubeVideoIds = trailerIds
    )
}

fun IgdbGenreDto.toDomain(): Genre {
    return Genre(
        id = id,
        name = name
    )
}

private fun String.toFullImageUrl(): String {
    val trimmed = trim()
    if (trimmed.startsWith("http")) {
        return trimmed
    }
    if (trimmed.startsWith("//")) {
        return "https:$trimmed"
    }
    return "https://$trimmed"
}

private fun String.toIgdbImageUrl(size: IgdbImageSize): String {
    val trimmed = trim()
    if (trimmed.isBlank()) {
        return ""
    }
    return "$IGDB_IMAGE_BASE_URL/t_${size.value}/$trimmed.jpg"
}

private fun Long.toIsoDateString(): String {
    val instant = Instant.ofEpochSecond(this)
    return DateTimeFormatter.ISO_LOCAL_DATE.withZone(ZoneOffset.UTC).format(instant)
}
