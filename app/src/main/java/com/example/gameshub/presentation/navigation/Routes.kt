package com.example.gameshub.presentation.navigation

object Routes {
    const val GAMES_LIST: String = "games_list"
    const val GAME_DETAILS: String = "game_details"
    const val GAME_ID_ARG: String = "gameId"
    fun createGameDetailsRoute(gameId: Int): String = "$GAME_DETAILS/$gameId"
}

