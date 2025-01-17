package com.nemislimus.crossvszero.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nemislimus.crossvszero.data.db.entities.PlayerEntity

@Dao
interface PlayersDao {

    @Insert(entity = PlayerEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPlayerToDatabase(entity: PlayerEntity)

    @Update(entity = PlayerEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePlayerInDatabase(entity: PlayerEntity)

    @Query("DELETE FROM players_table WHERE player_name = :playerName")
    suspend fun deletePlayerFromDatabase(playerName: String)

    @Query("SELECT * FROM players_table ORDER BY player_name")
    suspend fun getAllPlayers(): List<PlayerEntity>

    @Query("SELECT * FROM players_table WHERE player_name = :playerName")
    suspend fun getPlayerByName(playerName: String): PlayerEntity

}