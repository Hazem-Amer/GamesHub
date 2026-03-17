package com.example.gameshub.presentation.details

import com.example.gameshub.domain.model.GameDetails

sealed interface GameDetailsUiState {
    data object Loading : GameDetailsUiState
    data class Error(val message: String) : GameDetailsUiState
    data class Success(val details: GameDetails) : GameDetailsUiState
}

