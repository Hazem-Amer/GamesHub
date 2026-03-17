package com.example.gameshub.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gameshub.data.local.entity.CachedGameDetailsEntity

@Dao
interface GameDetailsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameDetails(details: CachedGameDetailsEntity)

    @Query("SELECT * FROM game_details WHERE id = :id")
    suspend fun getGameDetailsById(id: Int): CachedGameDetailsEntity?
}

