package com.example.gameshub.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gameshub.domain.usecase.GetGameDetailsUseCase
import com.example.gameshub.presentation.navigation.Routes
import com.example.gameshub.util.AppResult
import com.example.gameshub.util.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class GameDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getGameDetailsUseCase: GetGameDetailsUseCase,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {
    private val gameId: Int = checkNotNull(savedStateHandle[Routes.GAME_ID_ARG])
    private val mutableState: MutableStateFlow<GameDetailsState> = MutableStateFlow(GameDetailsState())
    val state: StateFlow<GameDetailsState> = mutableState.asStateFlow()

    init {
        executeLoadDetails()
    }

    fun executeLoadDetails() {
        mutableState.update { it.copy(uiState = GameDetailsUiState.Loading) }
        viewModelScope.launch(dispatcherProvider.io) {
            when (val result = getGameDetailsUseCase.execute(gameId)) {
                is AppResult.Success -> mutableState.update { it.copy(uiState = GameDetailsUiState.Success(result.value)) }
                is AppResult.Error -> mutableState.update { it.copy(uiState = GameDetailsUiState.Error(result.message)) }
            }
        }
    }
}

