package com.nemislimus.crossvszero.di

import com.nemislimus.crossvszero.data.ClassicGameRepositoryImpl
import com.nemislimus.crossvszero.data.db.CvzDatabase
import com.nemislimus.crossvszero.data.db.PlayersRepositoryDatabaseImpl
import com.nemislimus.crossvszero.domain.api.ClassicGameRepository
import com.nemislimus.crossvszero.domain.api.PlayersRepository
import org.koin.dsl.module

val dataModule = module {

    single<CvzDatabase> { CvzDatabase.getAppDatabase(get()) }

    single<ClassicGameRepository> { ClassicGameRepositoryImpl() }

    single<PlayersRepository> {
        PlayersRepositoryDatabaseImpl(
            get<CvzDatabase>().getPlayerDao()
        )
    }

}