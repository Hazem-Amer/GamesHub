package com.example.gameshub.presentation.games

sealed interface GamesUiState {
    data object Loading : GamesUiState
    data object PaginationLoading : GamesUiState
    data class Error(val message: String) : GamesUiState
    data object Empty : GamesUiState
    data object Success : GamesUiState
}

