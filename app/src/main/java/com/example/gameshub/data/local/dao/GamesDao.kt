package com.example.gameshub.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gameshub.data.local.entity.CachedGameEntity

@Dao
interface GamesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGames(games: List<CachedGameEntity>)

    @Query(
        """
        SELECT * FROM games
        WHERE (:genreId IS NULL AND genre_id IS NULL) OR genre_id = :genreId
        ORDER BY page_index ASC, id ASC
        """
    )
    suspend fun getGamesForGenre(genreId: Int?): List<CachedGameEntity>

    @Query(
        """
        SELECT * FROM games
        WHERE ((:genreId IS NULL AND genre_id IS NULL) OR genre_id = :genreId)
        AND page_index = :pageIndex
        ORDER BY id ASC
        """
    )
    suspend fun getGamesForGenreAndPage(genreId: Int?, pageIndex: Int): List<CachedGameEntity>
}

