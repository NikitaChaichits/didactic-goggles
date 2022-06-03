package com.example.vpn.domain.mapper

interface ModelMapper<E, M> {
    fun fromEntity(from: E): M
    fun toEntity(from: M): E
}