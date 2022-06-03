package com.example.vpn.domain.repository

import com.example.vpn.domain.model.response.ListResponse
import com.example.vpn.domain.result.Result

interface VpnRepository {
    suspend fun getServersList(): Result<ListResponse>
}