package com.example.vpn.domain.repository

import com.example.vpn.domain.model.ApiServer
import com.example.vpn.domain.result.Result
import retrofit2.Response

interface VpnRepository {
    suspend fun getServersList(): Result<List<ApiServer>>
    suspend fun getServerConfig(serverIp: String): Result<Response<String>>
}