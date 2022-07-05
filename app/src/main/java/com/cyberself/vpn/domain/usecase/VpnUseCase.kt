package com.cyberself.vpn.domain.usecase

import com.cyberself.vpn.domain.model.ApiServer
import com.cyberself.vpn.domain.repository.VpnRepository
import com.cyberself.vpn.domain.result.Result
import retrofit2.Response
import javax.inject.Inject

class VpnUseCase @Inject constructor(
    private val repository: VpnRepository
) {
    suspend fun getServersList(): Result<List<ApiServer>> {
        return repository.getServersList()
    }
    suspend fun getServerConfig(serverIp: String): Result<Response<String>> {
        return repository.getServerConfig(serverIp)
    }
}