package com.example.gameshub.data.remote.api

import com.example.gameshub.data.remote.dto.IgdbGameDto
import com.example.gameshub.data.remote.dto.IgdbGenreDto
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

interface IgdbApiService {
    @POST("games")
    suspend fun getGames(
        @Body body: RequestBody
    ): List<IgdbGameDto>

    @POST("genres")
    suspend fun getGenres(
        @Body body: RequestBody
    ): List<IgdbGenreDto>
}

