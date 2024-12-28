package com.nemislimus.crossvszero.data.db

import com.nemislimus.crossvszero.data.db.dao.PlayersDao
import com.nemislimus.crossvszero.data.db.entities.PlayerEntity.Companion.entityToPlayer
import com.nemislimus.crossvszero.data.db.entities.PlayerEntity.Companion.playerToEntity
import com.nemislimus.crossvszero.domain.api.PlayersRepository
import com.nemislimus.crossvszero.domain.models.Player

class PlayersRepositoryDatabaseImpl(
    private val playersDao: PlayersDao,
): PlayersRepository {

    override suspend fun addPlayer(player: Player) {
        playersDao.addPlayerToDatabase(player.playerToEntity())
    }

    override suspend fun deletePlayerByName(playerName: String) {
        playersDao.deletePlayerFromDatabase(playerName)
    }

    override suspend fun getAllPlayers(): List<Player> {
        return playersDao.getAllPlayers().map { it.entityToPlayer() }
    }

}