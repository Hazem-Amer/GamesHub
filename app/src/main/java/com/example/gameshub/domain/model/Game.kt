package com.example.gameshub.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Game(
    val id: Int,
    val name: String,
    val backgroundImageUrl: String?,
    val rating: Double
)

