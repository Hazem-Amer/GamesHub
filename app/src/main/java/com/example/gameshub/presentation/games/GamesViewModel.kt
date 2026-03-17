package com.example.gameshub.presentation.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gameshub.domain.model.Game
import com.example.gameshub.domain.usecase.GetGamesParams
import com.example.gameshub.domain.usecase.GetGamesUseCase
import com.example.gameshub.domain.usecase.GetGenresUseCase
import com.example.gameshub.util.AppResult
import com.example.gameshub.util.DispatcherProvider
import com.example.gameshub.util.NetworkConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class GamesViewModel @Inject constructor(
    private val getGamesUseCase: GetGamesUseCase,
    private val getGenresUseCase: GetGenresUseCase,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {
    private val mutableState: MutableStateFlow<GamesState> = MutableStateFlow(GamesState())
    val state: StateFlow<GamesState> = mutableState.asStateFlow()

    init {
        executeLoadGenresAndRefresh()
    }

    fun executeLoadGenresAndRefresh() {
        mutableState.update { it.copy(uiState = GamesUiState.Loading) }
        viewModelScope.launch(dispatcherProvider.io) {
            when (val result = getGenresUseCase.execute()) {
                is AppResult.Success -> {
                    val genreFilters = listOf(GenreFilter.All) + result.value.map { GenreFilter.createFromGenre(it) }
                    mutableState.update { it.copy(availableGenres = genreFilters) }
                    executeRefresh()
                }
                is AppResult.Error -> {
                    mutableState.update { it.copy(uiState = GamesUiState.Error(message = result.message)) }
                }
            }
        }
    }

    fun executeRefresh() {
        mutableState.update {
            it.copy(
                allGames = emptyList(),
                visibleGames = emptyList(),
                uiState = GamesUiState.Loading,
                nextPage = 1,
                canLoadMore = true,
                paginationErrorMessage = null
            )
        }
        executeLoadPage(page = 1, isPagination = false)
    }

    fun executeSelectGenre(genreFilter: GenreFilter) {
        mutableState.update {
            it.copy(
                selectedGenre = genreFilter,
                searchQuery = "",
                allGames = emptyList(),
                visibleGames = emptyList(),
                uiState = GamesUiState.Loading,
                nextPage = 1,
                canLoadMore = true,
                paginationErrorMessage = null
            )
        }
        executeLoadPage(page = 1, isPagination = false)
    }

    fun executeSearchQueryChange(query: String) {
        mutableState.update { currentState ->
            val updatedVisible = filterGames(games = currentState.allGames, query = query)
            val updatedUiState = when {
                currentState.uiState is GamesUiState.Loading -> currentState.uiState
                currentState.uiState is GamesUiState.Error -> currentState.uiState
                updatedVisible.isEmpty() -> GamesUiState.Empty
                else -> GamesUiState.Success
            }
            currentState.copy(
                searchQuery = query,
                visibleGames = updatedVisible,
                uiState = updatedUiState
            )
        }
    }

    fun executeLoadNextPage() {
        val currentState = mutableState.value
        val isLoading = currentState.uiState is GamesUiState.Loading
        val isPaginating = currentState.uiState is GamesUiState.PaginationLoading
        if (isLoading || isPaginating || !currentState.canLoadMore) {
            return
        }
        mutableState.update { it.copy(uiState = GamesUiState.PaginationLoading, paginationErrorMessage = null) }
        executeLoadPage(page = currentState.nextPage, isPagination = true)
    }

    fun executeConsumePaginationError() {
        mutableState.update { it.copy(paginationErrorMessage = null) }
    }

    private fun executeLoadPage(page: Int, isPagination: Boolean) {
        viewModelScope.launch(dispatcherProvider.io) {
            val currentState = mutableState.value
            val result = getGamesUseCase.execute(
                params = GetGamesParams(
                    page = page,
                    pageSize = NetworkConstants.DEFAULT_PAGE_SIZE,
                    genreId = currentState.selectedGenre.id
                )
            )
            when (result) {
                is AppResult.Success -> {
                    val updatedAllGames = if (isPagination) {
                        currentState.allGames + result.value
                    } else {
                        result.value
                    }
                    val updatedVisible = filterGames(updatedAllGames, currentState.searchQuery)
                    val canLoadMore = result.value.size >= NetworkConstants.DEFAULT_PAGE_SIZE
                    val nextPage = if (canLoadMore) page + 1 else page
                    val updatedUiState = when {
                        updatedVisible.isEmpty() -> GamesUiState.Empty
                        else -> GamesUiState.Success
                    }
                    mutableState.update {
                        it.copy(
                            allGames = updatedAllGames,
                            visibleGames = updatedVisible,
                            uiState = updatedUiState,
                            nextPage = nextPage,
                            canLoadMore = canLoadMore,
                            paginationErrorMessage = null
                        )
                    }
                }
                is AppResult.Error -> {
                    if (isPagination) {
                        mutableState.update {
                            it.copy(
                                uiState = if (it.visibleGames.isEmpty()) GamesUiState.Empty else GamesUiState.Success,
                                paginationErrorMessage = result.message
                            )
                        }
                        return@launch
                    }
                    mutableState.update {
                        it.copy(
                            uiState = GamesUiState.Error(message = result.message),
                            paginationErrorMessage = null
                        )
                    }
                }
            }
        }
    }

    private fun filterGames(games: List<Game>, query: String): List<Game> {
        val normalizedQuery = query.trim()
        if (normalizedQuery.isEmpty()) {
            return games
        }
        return games.filter { game ->
            game.name.contains(normalizedQuery, ignoreCase = true)
        }
    }
}

