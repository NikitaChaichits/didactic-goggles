package com.example.vpn.data.repository

import com.example.vpn.data.source.remote.RemoteDataSource
import com.example.vpn.domain.model.ApiServer
import com.example.vpn.domain.repository.VpnRepository
import com.example.vpn.domain.result.Result
import retrofit2.Response
import javax.inject.Inject

class VpnRepositoryImpl @Inject constructor(
    private val dataSource: RemoteDataSource
): VpnRepository {

    override suspend fun getServersList(): Result<List<ApiServer>> {
        return dataSource.getServersList()
    }

    override suspend fun getServerConfig(serverIp: String): Result<Response<String>> {
        return dataSource.getServerConfig(serverIp)
    }
}