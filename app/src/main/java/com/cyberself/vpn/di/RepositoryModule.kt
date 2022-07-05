package com.cyberself.vpn.di

import com.cyberself.vpn.data.repository.VpnRepositoryImpl
import com.cyberself.vpn.data.source.remote.RemoteDataSource
import com.cyberself.vpn.domain.repository.VpnRepository
import com.cyberself.vpn.domain.usecase.VpnUseCase
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