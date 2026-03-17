package com.example.gameshub.data.repository

import com.example.gameshub.data.local.dao.GameDetailsDao
import com.example.gameshub.data.local.dao.GamesDao
import com.example.gameshub.data.local.dao.GenresDao
import com.example.gameshub.data.local.mappers.toCachedEntity
import com.example.gameshub.data.local.mappers.toDomain
import com.example.gameshub.data.remote.api.IgdbApiService
import com.example.gameshub.data.remote.mappers.toDomain
import com.example.gameshub.data.remote.mappers.toDomainGame
import com.example.gameshub.data.remote.mappers.toDomainGameDetails
import com.example.gameshub.domain.model.Game
import com.example.gameshub.domain.model.GameDetails
import com.example.gameshub.domain.model.Genre
import com.example.gameshub.domain.repository.GamesRepository
import com.example.gameshub.util.NetworkStatusProvider
import javax.inject.Inject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class IgdbGamesRepository @Inject constructor(
    private val apiService: IgdbApiService,
    private val gamesDao: GamesDao,
    private val gameDetailsDao: GameDetailsDao,
    private val genresDao: GenresDao,
    private val networkStatusProvider: NetworkStatusProvider
) : GamesRepository {
    override suspend fun getGames(page: Int, pageSize: Int, genreId: Int?): List<Game> {
        val offset = (page - 1) * pageSize
        val fields = "fields id,name,rating,cover.image_id,cover.url;"
        val whereGenre = if (genreId != null) " where genres = ($genreId);" else ""
        val bodyString = "$fields$whereGenre sort rating desc; limit $pageSize; offset $offset;"
        val body = bodyString.toIgdbBody()
        val isOnline = networkStatusProvider.isOnline()
        if (isOnline) {
            try {
                val remoteGames = apiService.getGames(body).map { it.toDomainGame() }
                val cachedGamesEntities = remoteGames.map { it.toCachedEntity(pageIndex = page, genreId = genreId) }
                gamesDao.insertGames(cachedGamesEntities)
                return remoteGames
            } catch (err: Exception) {
                // fall through to cache
            }
        }
        val cachedEntities = gamesDao.getGamesForGenreAndPage(genreId = genreId, pageIndex = page)
        if (cachedEntities.isNotEmpty()) {
            return cachedEntities.map { it.toDomain() }
        }
        if (!isOnline) {
            return emptyList()
        }
        throw RuntimeException("Failed to load games and no cache available.")
    }

    override suspend fun getGameDetails(id: Int): GameDetails {
        val bodyString = "fields id,name,rating,first_release_date,summary,cover.image_id,cover.url,screenshots.image_id,videos.video_id; where id = $id; limit 1;"
        val body = bodyString.toIgdbBody()
        val isOnline = networkStatusProvider.isOnline()
        if (isOnline) {
            try {
                val result = apiService.getGames(body)
                val dto = result.firstOrNull() ?: error("Game not found")
                val details = dto.toDomainGameDetails()
                gameDetailsDao.insertGameDetails(details.toCachedEntity())
                return details
            } catch (err: Exception) {
                // fall through to cache
            }
        }
        val cachedEntity = gameDetailsDao.getGameDetailsById(id)
        if (cachedEntity != null) {
            return cachedEntity.toDomain()
        }
        throw RuntimeException("Failed to load game details and no cache available.")
    }

    override suspend fun getGenres(): List<Genre> {
        val bodyString = "fields id,name; sort name asc; limit 200;"
        val body = bodyString.toIgdbBody()
        val isOnline = networkStatusProvider.isOnline()
        if (isOnline) {
            try {
                val remoteGenres = apiService.getGenres(body).map { it.toDomain() }
                val cachedGenreEntities = remoteGenres.map { it.toCachedEntity() }
                genresDao.insertGenres(cachedGenreEntities)
                return remoteGenres
            } catch (err: Exception) {
                // fall through to cache
            }
        }
        val cachedEntities = genresDao.getAllGenres()
        if (cachedEntities.isNotEmpty()) {
            return cachedEntities.map { it.toDomain() }
        }
        if (!isOnline) {
            return emptyList()
        }
        throw RuntimeException("Failed to load genres and no cache available.")
    }
}

private fun String.toIgdbBody(): RequestBody {
    return toRequestBody("text/plain".toMediaType())
}

