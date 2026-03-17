package com.example.gameshub.presentation.games

import app.cash.turbine.test
import com.example.gameshub.domain.model.Game
import com.example.gameshub.domain.usecase.GetGamesParams
import com.example.gameshub.domain.usecase.GetGamesUseCase
import com.example.gameshub.domain.usecase.GetGenresUseCase
import com.example.gameshub.testutil.MainDispatcherRule
import com.example.gameshub.testutil.TestDispatcherProvider
import com.example.gameshub.util.AppResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GamesViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val mainDispatcherRule: MainDispatcherRule = MainDispatcherRule(testDispatcher)

    private val mockUseCase: GetGamesUseCase = mockk()
    private val mockGenresUseCase: GetGenresUseCase = mockk()

    @Test
    fun init_whenUseCaseSucceeds_emitsSuccessWithGames() = runTest(testDispatcher) {
        val returnedGames: List<Game> = listOf(
            Game(id = 1, name = "Elden Ring", backgroundImageUrl = null, rating = 4.9)
        )
        coEvery { mockGenresUseCase.execute() } returns AppResult.Success(emptyList())
        coEvery { mockUseCase.execute(any()) } returns AppResult.Success(returnedGames)
        val viewModel = GamesViewModel(
            getGamesUseCase = mockUseCase,
            getGenresUseCase = mockGenresUseCase,
            dispatcherProvider = TestDispatcherProvider(testDispatcher)
        )
        viewModel.state.test {
            val initial = awaitItem()
            assertEquals(GamesUiState.Loading, initial.uiState)
            advanceUntilIdle()
            var loaded = awaitItem()
            while (loaded.uiState is GamesUiState.Loading) {
                loaded = awaitItem()
            }
            assertEquals(GamesUiState.Success, loaded.uiState)
            assertEquals(returnedGames, loaded.allGames)
            assertEquals(returnedGames, loaded.visibleGames)
            cancelAndIgnoreRemainingEvents()
        }
        coVerify(exactly = 1) {
            mockUseCase.execute(
                GetGamesParams(
                    page = 1,
                    pageSize = 20,
                    genreId = null
                )
            )
        }
    }
}

