package com.cyberself.vpn.data.repository

import com.cyberself.vpn.data.source.remote.RemoteDataSource
import com.cyberself.vpn.domain.model.ApiServer
import com.cyberself.vpn.domain.repository.VpnRepository
import com.cyberself.vpn.domain.result.Result
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