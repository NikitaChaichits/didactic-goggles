package com.example.vpn.data.source.remote

import com.example.vpn.data.source.remote.api.VpnServerApi
import com.example.vpn.data.util.safeApiCall
import com.example.vpn.domain.model.ApiServer
import com.example.vpn.domain.result.Result
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val api: VpnServerApi
) {

    suspend fun getServersList(): Result<List<ApiServer>> {
        return safeApiCall { api.getServersList() }
    }

    suspend fun getServerConfig(serverIp: String): Result<Response<String>> {
        return safeApiCall { api.getConfig(serverIp) }
    }
}