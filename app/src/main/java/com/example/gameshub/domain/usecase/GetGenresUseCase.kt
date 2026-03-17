package com.example.gameshub.domain.usecase

import com.example.gameshub.domain.model.Genre
import com.example.gameshub.domain.repository.GamesRepository
import com.example.gameshub.util.AppResult
import java.io.IOException
import retrofit2.HttpException
import javax.inject.Inject

class GetGenresUseCase @Inject constructor(
    private val repository: GamesRepository
) {
    suspend fun execute(): AppResult<List<Genre>> {
        return try {
            AppResult.Success(repository.getGenres())
        } catch (err: IOException) {
            AppResult.Error(message = "Network error. Check your connection and try again.", cause = err)
        } catch (err: HttpException) {
            AppResult.Error(message = "Server error (${err.code()}). Please try again.", cause = err)
        } catch (err: Exception) {
            AppResult.Error(message = "Unexpected error. Please try again.", cause = err)
        }
    }
}

