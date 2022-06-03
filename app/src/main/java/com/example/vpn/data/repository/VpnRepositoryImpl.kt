package com.example.vpn.data.repository

import com.example.vpn.data.source.remote.RemoteDataSource
import com.example.vpn.domain.model.response.ListResponse
import com.example.vpn.domain.repository.VpnRepository
import com.example.vpn.domain.result.Result
import javax.inject.Inject

class VpnRepositoryImpl @Inject constructor(
    private val dataSource: RemoteDataSource
): VpnRepository {

    override suspend fun getServersList(): Result<ListResponse> {
        return dataSource.getServersList()
    }
}