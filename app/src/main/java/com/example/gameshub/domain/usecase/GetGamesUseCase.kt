package com.example.gameshub.domain.usecase

import com.example.gameshub.domain.model.Game
import com.example.gameshub.domain.repository.GamesRepository
import com.example.gameshub.util.AppResult
import java.io.IOException
import retrofit2.HttpException
import javax.inject.Inject

data class GetGamesParams(
    val page: Int,
    val pageSize: Int,
    val genreId: Int?
)

class GetGamesUseCase @Inject constructor(
    private val repository: GamesRepository
) {
    suspend fun execute(params: GetGamesParams): AppResult<List<Game>> {
        return try {
            val games = repository.getGames(
                page = params.page,
                pageSize = params.pageSize,
                genreId = params.genreId
            )
            AppResult.Success(games)
        } catch (err: IOException) {
            AppResult.Error(message = "Network error. Check your connection and try again.", cause = err)
        } catch (err: HttpException) {
            AppResult.Error(message = "Server error (${err.code()}). Please try again.", cause = err)
        } catch (err: Exception) {
            AppResult.Error(message = "Unexpected error. Please try again.", cause = err)
        }
    }
}

