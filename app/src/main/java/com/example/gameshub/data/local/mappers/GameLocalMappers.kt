package com.example.gameshub.data.local.mappers

import com.example.gameshub.data.local.entity.CachedGameDetailsEntity
import com.example.gameshub.data.local.entity.CachedGameEntity
import com.example.gameshub.data.local.entity.CachedGenreEntity
import com.example.gameshub.domain.model.Game
import com.example.gameshub.domain.model.GameDetails
import com.example.gameshub.domain.model.Genre
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

private val moshi: Moshi = Moshi.Builder().build()
private val listOfStringType = Types.newParameterizedType(List::class.java, String::class.java)
private val listOfStringAdapter = moshi.adapter<List<String>>(listOfStringType)

fun Game.toCachedEntity(pageIndex: Int, genreId: Int?): CachedGameEntity {
    return CachedGameEntity(
        id = id,
        name = name,
        backgroundImageUrl = backgroundImageUrl,
        rating = rating,
        genreId = genreId,
        pageIndex = pageIndex
    )
}

fun CachedGameEntity.toDomain(): Game {
    return Game(
        id = id,
        name = name,
        backgroundImageUrl = backgroundImageUrl,
        rating = rating
    )
}

fun GameDetails.toCachedEntity(): CachedGameDetailsEntity {
    return CachedGameDetailsEntity(
        id = id,
        name = name,
        backgroundImageUrl = backgroundImageUrl,
        released = released,
        rating = rating,
        description = description,
        screenshotUrlsJson = listOfStringAdapter.toJson(screenshotImageUrls),
        trailerIdsJson = listOfStringAdapter.toJson(trailerYoutubeVideoIds)
    )
}

fun CachedGameDetailsEntity.toDomain(): GameDetails {
    val screenshots = listOfStringAdapter.fromJson(screenshotUrlsJson).orEmpty()
    val trailers = listOfStringAdapter.fromJson(trailerIdsJson).orEmpty()
    return GameDetails(
        id = id,
        name = name,
        backgroundImageUrl = backgroundImageUrl,
        released = released,
        rating = rating,
        description = description,
        screenshotImageUrls = screenshots,
        trailerYoutubeVideoIds = trailers
    )
}

fun Genre.toCachedEntity(): CachedGenreEntity {
    return CachedGenreEntity(
        id = id,
        name = name
    )
}

fun CachedGenreEntity.toDomain(): Genre {
    return Genre(
        id = id,
        name = name
    )
}

