package com.example.gameshub.data.repository

import com.example.gameshub.data.remote.api.IgdbApiService
import com.example.gameshub.data.remote.mappers.toDomain
import com.example.gameshub.data.remote.mappers.toDomainGame
import com.example.gameshub.data.remote.mappers.toDomainGameDetails
import com.example.gameshub.domain.model.Game
import com.example.gameshub.domain.model.GameDetails
import com.example.gameshub.domain.model.Genre
import com.example.gameshub.domain.repository.GamesRepository
import javax.inject.Inject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class IgdbGamesRepository @Inject constructor(
    private val apiService: IgdbApiService
) : GamesRepository {
    override suspend fun getGames(page: Int, pageSize: Int, genreId: Int?): List<Game> {
        val offset = (page - 1) * pageSize
        val fields = "fields id,name,rating,cover.image_id,cover.url;"
        val whereGenre = if (genreId != null) " where genres = ($genreId);" else ""
        val bodyString = "$fields$whereGenre sort rating desc; limit $pageSize; offset $offset;"
        val body = bodyString.toIgdbBody()
        return apiService.getGames(body).map { it.toDomainGame() }
    }

    override suspend fun getGameDetails(id: Int): GameDetails {
        val bodyString = "fields id,name,rating,first_release_date,summary,cover.image_id,cover.url,screenshots.image_id,videos.video_id; where id = $id; limit 1;"
        val body = bodyString.toIgdbBody()
        val result = apiService.getGames(body)
        val dto = result.firstOrNull() ?: error("Game not found")
        return dto.toDomainGameDetails()
    }

    override suspend fun getGenres(): List<Genre> {
        val bodyString = "fields id,name; sort name asc; limit 200;"
        val body = bodyString.toIgdbBody()
        return apiService.getGenres(body).map { it.toDomain() }
    }
}

private fun String.toIgdbBody(): RequestBody {
    return toRequestBody("text/plain".toMediaType())
}

