package com.example.vpn.data.source.remote

import com.example.vpn.data.source.remote.api.VpnServerApi
import com.example.vpn.data.util.safeApiCall
import com.example.vpn.domain.model.response.ListResponse
import com.example.vpn.domain.result.Result
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val api: VpnServerApi
) {

    suspend fun getServersList(): Result<ListResponse> {
        return safeApiCall { api.getServersList() }
    }
}