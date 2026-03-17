package com.example.gameshub.di

import com.example.gameshub.util.AndroidNetworkStatusProvider
import com.example.gameshub.util.NetworkStatusProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkStatusModule {
    @Binds
    @Singleton
    abstract fun bindNetworkStatusProvider(
        impl: AndroidNetworkStatusProvider
    ): NetworkStatusProvider
}

