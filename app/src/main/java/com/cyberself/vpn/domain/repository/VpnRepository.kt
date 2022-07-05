package com.cyberself.vpn.domain.repository

import com.cyberself.vpn.domain.model.ApiServer
import com.cyberself.vpn.domain.result.Result
import retrofit2.Response

interface VpnRepository {
    suspend fun getServersList(): Result<List<ApiServer>>
    suspend fun getServerConfig(serverIp: String): Result<Response<String>>
}