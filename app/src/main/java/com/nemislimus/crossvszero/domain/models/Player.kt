package com.nemislimus.crossvszero.domain.models

data class Player(
    val id: Long, // Id for database
    val name: String,
    val victories: Int,
    val defeats: Int,
)
