package com.example.gameshub.data.remote.api

import okhttp3.Interceptor
import okhttp3.Response

class IgdbAuthInterceptor(
    private val clientId: String,
    private val accessToken: String
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val updatedRequest = request.newBuilder()
            .addHeader("Client-ID", clientId)
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Accept", "application/json")
            .build()
        return chain.proceed(updatedRequest)
    }
}

