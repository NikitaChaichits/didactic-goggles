package com.example.vpn.common

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("error") val error: Int
)
