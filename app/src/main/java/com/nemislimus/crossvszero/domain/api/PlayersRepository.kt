package com.nemislimus.crossvszero.domain.api

import com.nemislimus.crossvszero.domain.models.Player

interface PlayersRepository {
    suspend fun addPlayer(player: Player)
    suspend fun deletePlayerByName(playerName: String)
    suspend fun getAllPlayers(): List<Player>
}