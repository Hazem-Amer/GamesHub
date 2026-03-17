package com.example.gameshub.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_details")
data class CachedGameDetailsEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "background_image_url")
    val backgroundImageUrl: String?,
    @ColumnInfo(name = "released")
    val released: String?,
    @ColumnInfo(name = "rating")
    val rating: Double,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "screenshot_urls_json")
    val screenshotUrlsJson: String,
    @ColumnInfo(name = "trailer_ids_json")
    val trailerIdsJson: String
)

