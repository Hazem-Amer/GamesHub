package com.example.gameshub.data.repository

import com.example.gameshub.data.local.dao.GameDetailsDao
import com.example.gameshub.data.local.dao.GamesDao
import com.example.gameshub.data.local.dao.GenresDao
import com.example.gameshub.data.local.entity.CachedGameDetailsEntity
import com.example.gameshub.data.local.entity.CachedGameEntity
import com.example.gameshub.data.local.entity.CachedGenreEntity
import com.example.gameshub.data.remote.api.IgdbApiService
import com.example.gameshub.data.remote.dto.IgdbGameDto
import com.example.gameshub.data.remote.dto.IgdbGenreDto
import com.example.gameshub.domain.model.Game
import com.example.gameshub.util.NetworkStatusProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class IgdbGamesRepositoryTest {
    private val apiService: IgdbApiService = mockk()
    private val gamesDao: GamesDao = mockk(relaxed = true)
    private val gameDetailsDao: GameDetailsDao = mockk(relaxed = true)
    private val genresDao: GenresDao = mockk(relaxed = true)
    private val networkStatusProvider: NetworkStatusProvider = mockk()

    private val repository = IgdbGamesRepository(
        apiService = apiService,
        gamesDao = gamesDao,
        gameDetailsDao = gameDetailsDao,
        genresDao = genresDao,
        networkStatusProvider = networkStatusProvider
    )

    @Test
    fun getGames_online_success_savesToCacheAndReturnsRemote() = runBlocking {
        coEvery { networkStatusProvider.isOnline() } returns true
        val remote = listOf(
            IgdbGameDto(id = 1, name = "Test", rating = 4.5)
        )
        coEvery { apiService.getGames(any()) } returns remote

        val result = repository.getGames(page = 1, pageSize = 20, genreId = null)

        assertEquals(listOf(Game(id = 1, name = "Test", backgroundImageUrl = null, rating = 4.5)), result)
        coVerify { gamesDao.insertGames(match { it.size == 1 && it[0].id == 1 }) }
    }

    @Test
    fun getGames_offline_usesCache() = runBlocking {
        coEvery { networkStatusProvider.isOnline() } returns false
        val cachedEntities = listOf(
            CachedGameEntity(
                id = 1,
                name = "Cached",
                backgroundImageUrl = null,
                rating = 4.0,
                genreId = null,
                pageIndex = 1
            )
        )
        coEvery { gamesDao.getGamesForGenreAndPage(null, 1) } returns cachedEntities

        val result = repository.getGames(page = 1, pageSize = 20, genreId = null)

        assertEquals(1, result.size)
        assertEquals("Cached", result[0].name)
    }

    @Test
    fun getGenres_online_success_savesToCache() = runBlocking {
        coEvery { networkStatusProvider.isOnline() } returns true
        val remoteGenres = listOf(IgdbGenreDto(id = 10, name = "Action"))
        coEvery { apiService.getGenres(any()) } returns remoteGenres

        val result = repository.getGenres()

        assertEquals(1, result.size)
        assertEquals(10, result[0].id)
        coVerify { genresDao.insertGenres(match { it.size == 1 && it[0].id == 10 }) }
    }
}

