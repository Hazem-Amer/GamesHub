package com.example.gameshub.domain.repository

import com.example.gameshub.domain.model.Game
import com.example.gameshub.domain.model.GameDetails
import com.example.gameshub.domain.model.Genre

interface GamesRepository {
    suspend fun getGames(
        page: Int,
        pageSize: Int,
        genreId: Int?
    ): List<Game>

    suspend fun getGameDetails(
        id: Int
    ): GameDetails

    suspend fun getGenres(): List<Genre>
}

