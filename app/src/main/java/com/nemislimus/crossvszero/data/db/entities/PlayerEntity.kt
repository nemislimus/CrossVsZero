package com.nemislimus.crossvszero.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nemislimus.crossvszero.data.db.entities.PlayerEntity.Companion.TABLE_NAME
import com.nemislimus.crossvszero.domain.models.Player

@Entity(
    tableName = TABLE_NAME
)
data class PlayerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "player_name")
    val playerName: String,
    val victories: Int,
    val defeats: Int,
) {

    companion object {
        const val TABLE_NAME = "players_table"

        fun PlayerEntity.entityToPlayer(): Player {
            return Player(
                name = this.playerName,
                victories = this.victories,
                defeats = this.defeats,
            )
        }

        fun Player.playerToEntity(): PlayerEntity {
            return PlayerEntity(
                id = 0L,
                playerName = this.name,
                victories = this.victories,
                defeats = this.defeats,
            )
        }
    }
}
