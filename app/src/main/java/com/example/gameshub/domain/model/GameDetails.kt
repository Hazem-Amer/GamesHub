package com.example.gameshub.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class GameDetails(
    val id: Int,
    val name: String,
    val backgroundImageUrl: String?,
    val released: String?,
    val rating: Double,
    val description: String,
    val screenshotImageUrls: List<String>,
    val trailerYoutubeVideoIds: List<String>
)

