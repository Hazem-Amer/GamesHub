package com.example.gameshub.domain.usecase

import com.example.gameshub.domain.model.Game
import com.example.gameshub.domain.repository.GamesRepository
import com.example.gameshub.util.AppResult
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetGamesUseCaseTest {
    private val mockRepository: GamesRepository = mockk()
    private val useCase: GetGamesUseCase = GetGamesUseCase(repository = mockRepository)

    @Test
    fun execute_whenRepositoryReturnsGames_returnsSuccess() = runTest {
        val expectedGames: List<Game> = listOf(
            Game(id = 1, name = "Game 1", backgroundImageUrl = null, rating = 4.2),
            Game(id = 2, name = "Game 2", backgroundImageUrl = "https://example.com/img.png", rating = 3.6)
        )
        coEvery { mockRepository.getGames(page = 1, pageSize = 20, genreId = null) } returns expectedGames
        val actualResult = useCase.execute(GetGamesParams(page = 1, pageSize = 20, genreId = null))
        assertTrue(actualResult is AppResult.Success)
        val actualGames = (actualResult as AppResult.Success).value
        assertEquals(expectedGames, actualGames)
    }
}

