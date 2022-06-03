package com.example.vpn.di

import com.example.vpn.data.repository.VpnRepositoryImpl
import com.example.vpn.data.source.remote.RemoteDataSource
import com.example.vpn.domain.repository.VpnRepository
import com.example.vpn.domain.usecase.VpnUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideVpnRepository(remoteDataSource: RemoteDataSource): VpnRepository =
        VpnRepositoryImpl(remoteDataSource)

    @Provides
    fun provideVpnUseCase(repository: VpnRepository): VpnUseCase = VpnUseCase(repository)
}