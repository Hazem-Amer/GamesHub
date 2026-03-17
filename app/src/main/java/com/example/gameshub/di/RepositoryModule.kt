package com.example.gameshub.di

import com.example.gameshub.data.repository.IgdbGamesRepository
import com.example.gameshub.domain.repository.GamesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindGamesRepository(
        repository: IgdbGamesRepository
    ): GamesRepository
}

