package com.example.vpn.data.source.remote.api

import com.example.vpn.data.constants.RemoteConstants.AUTHORIZATION
import com.example.vpn.data.constants.RemoteConstants.GET_CONFIG
import com.example.vpn.data.constants.RemoteConstants.GET_LIST
import com.example.vpn.data.constants.RemoteConstants.KEY
import com.example.vpn.domain.model.ApiServer
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface VpnServerApi {

    @GET(GET_LIST)
    suspend fun getServersList(): List<ApiServer>

    @Headers("$AUTHORIZATION: $KEY")
    @GET(GET_CONFIG)
    suspend fun getConfig(@Path("ip") serverIp: String): Response<String>
}