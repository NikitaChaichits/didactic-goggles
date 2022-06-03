package com.example.vpn.data.vpn.interfaces

import com.example.vpn.domain.model.Server

interface ChangeServer {
    fun newServer(server: Server?)
}