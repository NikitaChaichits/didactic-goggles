package com.example.vpn.domain.model.response

import com.example.vpn.domain.model.ApiServer

data class ListResponse(
    val articles : List<ApiServer>
)
