package com.example.vpn.domain.repository

import com.example.vpn.domain.model.ApiServer
import com.example.vpn.domain.model.ConfigResponse
import com.example.vpn.domain.model.Server
import com.example.vpn.domain.result.Result
import retrofit2.Call

interface VpnRepository {
    suspend fun getServersList(): Result<List<ApiServer>>
    suspend fun getServerConfig(serverIp: String): Result<Call<ConfigResponse>>
}