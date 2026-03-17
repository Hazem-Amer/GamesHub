package com.example.gameshub.presentation.games

import com.example.gameshub.domain.model.Game

data class GamesState(
    val allGames: List<Game> = emptyList(),
    val visibleGames: List<Game> = emptyList(),
    val searchQuery: String = "",
    val selectedGenre: GenreFilter = GenreFilter.All,
    val availableGenres: List<GenreFilter> = listOf(GenreFilter.All),
    val uiState: GamesUiState = GamesUiState.Loading,
    val nextPage: Int = 1,
    val canLoadMore: Boolean = true,
    val paginationErrorMessage: String? = null
)

