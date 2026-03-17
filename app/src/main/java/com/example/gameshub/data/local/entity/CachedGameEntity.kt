package com.example.gameshub.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "games",
    indices = [
        Index(value = ["genre_id", "page_index"])
    ]
)
data class CachedGameEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "background_image_url")
    val backgroundImageUrl: String?,
    @ColumnInfo(name = "rating")
    val rating: Double,
    @ColumnInfo(name = "genre_id")
    val genreId: Int?,
    @ColumnInfo(name = "page_index")
    val pageIndex: Int
)

