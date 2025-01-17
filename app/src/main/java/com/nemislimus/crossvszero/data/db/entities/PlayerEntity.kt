package com.nemislimus.crossvszero.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nemislimus.crossvszero.data.db.entities.PlayerEntity.Companion.TABLE_NAME
import com.nemislimus.crossvszero.domain.models.Player

@Entity(
    tableName = TABLE_NAME,
    indices = [Index(value = ["player_name"], unique = true)]
)
data class PlayerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "player_name")
    val playerName: String,
    val playerVictories: Int,
    val playerDefeats: Int,
) {

    companion object {
        const val TABLE_NAME = "players_table"

        fun PlayerEntity.entityToPlayer(): Player {
            return Player(
                id = this.id,
                name = this.playerName,
                victories = this.playerVictories,
                defeats = this.playerDefeats,
            )
        }

        fun Player.playerToEntity(): PlayerEntity {
            return PlayerEntity(
                id = this.id,
                playerName = this.name,
                playerVictories = this.victories,
                playerDefeats = this.defeats,
            )
        }
    }
}
