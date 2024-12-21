package com.nemislimus.crossvszero.di

import com.nemislimus.crossvszero.data.ClassicGameRepositoryImpl
import com.nemislimus.crossvszero.domain.api.ClassicGameRepository
import org.koin.dsl.module

val dataModule = module {

    single<ClassicGameRepository> { ClassicGameRepositoryImpl() }

}