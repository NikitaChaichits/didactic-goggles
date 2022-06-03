package com.example.vpn.data.source.remote.api

import com.example.vpn.data.constants.RemoteConstants.AUTHORIZATION
import com.example.vpn.data.constants.RemoteConstants.GET_CONFIG
import com.example.vpn.data.constants.RemoteConstants.GET_LIST
import com.example.vpn.data.constants.RemoteConstants.KEY
import com.example.vpn.domain.model.response.ConfigResponse
import com.example.vpn.domain.model.response.ListResponse
import retrofit2.http.GET
import retrofit2.http.Headers

interface VpnServerApi {

    @GET(GET_LIST)
    suspend fun getServersList(): ListResponse

    @Headers("$AUTHORIZATION: $KEY")
    @GET(GET_CONFIG)
    suspend fun getConfig(): ConfigResponse
}