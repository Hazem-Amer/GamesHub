package com.example.gameshub.di

import android.content.Context
import androidx.room.Room
import com.example.gameshub.data.local.GamesDatabase
import com.example.gameshub.data.local.dao.GameDetailsDao
import com.example.gameshub.data.local.dao.GamesDao
import com.example.gameshub.data.local.dao.GenresDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideGamesDatabase(
        @ApplicationContext context: Context
    ): GamesDatabase {
        return Room.databaseBuilder(
            context,
            GamesDatabase::class.java,
            "gameshub.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideGamesDao(database: GamesDatabase): GamesDao {
        return database.gamesDao()
    }

    @Provides
    @Singleton
    fun provideGameDetailsDao(database: GamesDatabase): GameDetailsDao {
        return database.gameDetailsDao()
    }

    @Provides
    @Singleton
    fun provideGenresDao(database: GamesDatabase): GenresDao {
        return database.genresDao()
    }
}

