package com.example.gameshub.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gameshub.data.local.entity.CachedGenreEntity

@Dao
interface GenresDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenres(genres: List<CachedGenreEntity>)

    @Query("SELECT * FROM genres ORDER BY name ASC")
    suspend fun getAllGenres(): List<CachedGenreEntity>
}

