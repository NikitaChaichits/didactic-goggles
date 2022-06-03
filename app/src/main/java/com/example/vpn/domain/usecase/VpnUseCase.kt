package com.example.vpn.domain.usecase

import com.example.vpn.domain.model.response.ListResponse
import com.example.vpn.domain.repository.VpnRepository
import com.example.vpn.domain.result.Result
import javax.inject.Inject

class VpnUseCase @Inject constructor(
    private val repository: VpnRepository
) {
    suspend fun getServersList(): Result<ListResponse> {
        return repository.getServersList()
    }
}