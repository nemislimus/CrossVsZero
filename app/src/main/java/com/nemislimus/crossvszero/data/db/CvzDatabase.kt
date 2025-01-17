package com.nemislimus.crossvszero.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nemislimus.crossvszero.data.db.dao.PlayersDao
import com.nemislimus.crossvszero.data.db.entities.PlayerEntity

@Database(
entities = [PlayerEntity::class],
    version = 1
)
abstract class CvzDatabase : RoomDatabase() {

    abstract fun getPlayerDao() : PlayersDao

    companion object {
        fun getAppDatabase(context: Context): CvzDatabase {
            return Room.databaseBuilder(
                context,
                CvzDatabase::class.java,
                "CVZ_database.db"
            )
                .fallbackToDestructiveMigration() // Clear Db on update
                .build()
        }
    }
}