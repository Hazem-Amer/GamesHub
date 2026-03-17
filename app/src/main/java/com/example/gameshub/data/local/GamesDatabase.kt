package com.example.gameshub.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.gameshub.data.local.dao.GameDetailsDao
import com.example.gameshub.data.local.dao.GamesDao
import com.example.gameshub.data.local.dao.GenresDao
import com.example.gameshub.data.local.entity.CachedGameDetailsEntity
import com.example.gameshub.data.local.entity.CachedGameEntity
import com.example.gameshub.data.local.entity.CachedGenreEntity

@Database(
    entities = [
        CachedGameEntity::class,
        CachedGameDetailsEntity::class,
        CachedGenreEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class GamesDatabase : RoomDatabase() {
    abstract fun gamesDao(): GamesDao
    abstract fun gameDetailsDao(): GameDetailsDao
    abstract fun genresDao(): GenresDao
}

